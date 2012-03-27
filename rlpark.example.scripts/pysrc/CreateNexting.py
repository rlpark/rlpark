import time
import zepy
from rlpark.plugin.irobot.robots import CreateRobot 
from zephyr.plugin.core.api.synchronization import Chrono, Clock
from rltoys.algorithms.representations.acting import RandomPolicy
from rlpark.plugin.irobot.data import CreateAction
from java.util import Random
from rltoys.algorithms.representations.tilescoding import TileCodersNoHashing
from rltoys.demons import DemonScheduler, RewardFunction, PredictionDemon,\
    PredictionDemonVerifier
from rltoys.algorithms.learning.predictions.td import TDLambda
from zephyr import ZephyrPlotting
from rltoys.algorithms.representations import ObsHistory
from zephyr.plugin.core.api.parsing import LabeledCollection
from zephyr.plugin.core.api import Zephyr
from zephyr.plugin.core.api.labels import Labeled
from zephyr.plugin.core.api.monitoring.fileloggers import TimedFileLogger

class SensorRewardFunction(RewardFunction, Labeled):
    def __init__(self, legend, label):
        self.rewardLabel = label
        self.index = legend.indexOf(label)
        self.rewardValue = 0
        
    def update(self, o_tp1):
        self.rewardValue = o_tp1[self.index]
        
    def reward(self):
        return self.rewardValue
    
    def label(self):
        return self.rewardLabel


class DemonExperiment(LabeledCollection):
    Latency = 100 #milliseconds
    HistoryLength = 10
    sensorsOfInterest = ["CliffSignalLeft","CliffSignalFrontLeft","CliffSignalFrontRight","CliffSignalRight"]
    demonToData = {}
    
    def __init__(self):
        self.logfile = TimedFileLogger('/tmp/log.crtrlog')
        self.environment = CreateRobot()
        self.environment.fullMode()
        self.latencyTimer = Chrono()
        self.clock = Clock("CreateNexting")
        self.rewards = self.createRewardFunction()
        self.actions = [ CreateAction(-200,+200) ]
        self.behaviourPolicy = RandomPolicy(Random(0), self.actions)
        self.obsHistory = ObsHistory(10, self.environment.legend())
        self.representation = TileCodersNoHashing(self.obsHistory.historyVectorSize(), 0, 4096)
        self.representation.includeActiveFeature()
        for name in self.sensorsOfInterest:
            for timeShift in range(self.HistoryLength):
                indexes = self.obsHistory.selectIndexes(timeShift, name)
                self.representation.addTileCoder(indexes,64,8)
        self.demons = DemonScheduler()
        self.verifiers = []
        for rewardFunction in self.rewards:
            for gamma in [0, 0.5, 0.75, 7/8., 15/16.]:
                demon = self.createOnPolicyPredictionDemon(rewardFunction,gamma)
                verifier = PredictionDemonVerifier(demon)
                self.verifiers.append(verifier)
                self.demons.add(demon)
                self.demonToData[demon] = (verifier, rewardFunction.label() + str(gamma))
        self.x_t = None

    def label(self, index):
        return self.demons.demons().get(index).label()
        
    def createRewardFunction(self):
        legend = self.environment.legend()
        return list(SensorRewardFunction(legend, label) for label in self.sensorsOfInterest)

    def createOnPolicyPredictionDemon(self, rewardFunction, gamma):
        alpha = .1 / self.representation.nbActive()
        nbFeatures = self.representation.vectorSize()
        lambda_= 1.0
        td = TDLambda(lambda_, gamma, alpha, nbFeatures)
        return PredictionDemon(rewardFunction, td)
        
    def learn(self, a_t, o_tp1):
        for rewardFunction in self.rewards:
            rewardFunction.update(o_tp1)
        ho_tp1 = self.obsHistory.update(o_tp1)
        x_tp1 = self.representation.project(ho_tp1)
        self.demons.update(self.x_t, a_t, x_tp1)
        for verifier in self.verifiers:
            verifier.update(False)
        self.x_t = x_tp1
        
    def run(self):
        a_t = None
        while not self.environment.isClosed():
            self.clock.tick()
            self.logfile.update()
            self.latencyTimer.start()
            o_tp1 = self.environment.waitNewObs()
            self.learn(a_t, o_tp1)
            a_tp1 = self.behaviourPolicy.decide(None)
            self.environment.sendAction(a_tp1)
            a_t = a_tp1
            waitingTime = self.Latency - self.latencyTimer.getCurrentMillis()
            if waitingTime > 0:
                time.sleep(waitingTime / 1000.)
                
    def zephyrize(self):
        Zephyr.registerLabeledCollection(self, "demons", "")
        monitoredList = [self.verifiers, self.environment, self.demons]
        for monitored in monitoredList:
            zepy.advertise(monitored, self.clock)
            self.logfile.add(monitored, 0)
        ZephyrPlotting.createMonitor(self.clock)
        for rewardFunction in self.rewards:
            zepy.monattr(rewardFunction, 'rewardValue', clock = self.clock, label = rewardFunction.label())
                

if __name__ == '__main__':
    experiment = DemonExperiment()
    experiment.zephyrize()
    experiment.run()
