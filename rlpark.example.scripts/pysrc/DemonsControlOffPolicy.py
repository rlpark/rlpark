import time
import zepy
from rlpark.plugin.critterbot.environment import CritterbotSimulator 
from zephyr.plugin.core.api.synchronization import Chrono
from rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers import RandomPolicy
from rlpark.plugin.critterbot.actions import XYThetaAction
from java.util import Random
from rlpark.plugin.rltoys.algorithms.representations.tilescoding import TileCodersNoHashing
from rlpark.plugin.rltoys.horde.functions import RewardFunction
from rlpark.plugin.rltoys.horde.demons import DemonScheduler
from rlpark.plugin.rltoys.algorithms.functions.stateactions import TabularAction
from rlpark.plugin.rltoys.algorithms.control.gq import GQ
from rlpark.plugin.rltoys.algorithms.control.acting import Greedy
from rlpark.plugin.rltoys.algorithms.control.gq import GreedyGQ
from rlpark.plugin.rltoys.horde.demons import ControlOffPolicyDemon

class SensorRewardFunction(RewardFunction):
    def __init__(self, legend, label):
        self.label = "reward" + label
        self.index = legend.indexOf(label)
        self.rewardValue = 0
        
    def update(self, o_tp1):
        self.rewardValue = o_tp1[self.index]
        
    def reward(self):
        return self.rewardValue

class DemonExperiment(object):
    Latency = 100 #s
    
    def __init__(self):
        command = CritterbotSimulator.startSimulator()
        self.environment = CritterbotSimulator(command)
        self.latencyTimer = Chrono()
        self.rewards = self.createRewardFunction()
        self.actions = XYThetaAction.sevenActions()
        self.behaviourPolicy = RandomPolicy(Random(0), self.actions)
        self.representation = TileCodersNoHashing(self.environment.legend().nbLabels(), -2000, 2000)
        self.representation.includeActiveFeature()
        self.demonsScheduler = DemonScheduler()
        self.demons = []
        for rewardFunction in self.rewards:
            self.demons.append(self.createOffPolicyControlDemon(rewardFunction))
        self.x_t = None
        self.clock = zepy.clock("Horde Off-policy Control demons")

    def createRewardFunction(self):
        legend = self.environment.legend()
        return [ SensorRewardFunction(legend, 'MotorCurrent0'),
                 SensorRewardFunction(legend, 'MotorCurrent1'),
                 SensorRewardFunction(legend, 'MotorCurrent2') ]

    def createOffPolicyControlDemon(self, rewardFunction):
        toStateAction = TabularAction(self.actions, self.representation.vectorNorm(), self.representation.vectorSize())
        nbFeatures = toStateAction.vectorSize()
        lambda_ = 0.1
        beta = .1
        alpha_v = .1 / toStateAction.vectorNorm()
        alpha_w = .001 / toStateAction.vectorNorm()
        gq = GQ(alpha_v, alpha_w, beta , lambda_, nbFeatures)
        targetPolicy = Greedy(gq, self.actions, toStateAction)
        controlGQ = GreedyGQ(gq, self.actions, toStateAction, targetPolicy, self.behaviourPolicy)
        return ControlOffPolicyDemon(rewardFunction, controlGQ)
        
    def learn(self, a_t, o_tp1):
        for rewardFunction in self.rewards:
            rewardFunction.update(o_tp1)
        x_tp1 = self.representation.project(o_tp1)
        self.demonsScheduler.update(self.demons, self.x_t, a_t, x_tp1)
        self.x_t = x_tp1
        
    def run(self):
        a_t = None
        while self.clock.tick():
            self.latencyTimer.start()
            o_tp1 = self.environment.waitNewObs()
            self.learn(a_t, o_tp1)
            a_tp1 = self.behaviourPolicy.decide(None)
            self.environment.sendAction(a_tp1)
            a_t = a_tp1
            waitingTime = self.Latency - self.latencyTimer.getCurrentMillis()
            if waitingTime > 0:
                time.sleep(waitingTime / 1000.0)
        self.environment.close()
                
    def zephyrize(self):
        zepy.advertise(self.clock, self.environment)
        zepy.advertise(self.clock, self.demonsScheduler)
        for rewardFunction in self.rewards:
            zepy.monattr(self.clock, rewardFunction, 'rewardValue', label = rewardFunction.label)
                

if __name__ == '__main__':
    experiment = DemonExperiment()
    experiment.zephyrize()
    experiment.run()
