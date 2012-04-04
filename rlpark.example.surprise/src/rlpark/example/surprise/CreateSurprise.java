package rlpark.example.surprise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.data.IRobotSongs;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.acting.ActionPolicy;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.envio.states.AgentState;
import rlpark.plugin.rltoys.horde.Horde;
import rlpark.plugin.rltoys.horde.Surprise;
import rlpark.plugin.rltoys.horde.demons.Demon;
import rlpark.plugin.rltoys.horde.demons.PredictionDemon;
import rlpark.plugin.rltoys.horde.demons.PredictionOffPolicyDemon;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.horde.functions.RewardObservationFunction;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class CreateSurprise implements Runnable {
  static final private int SurpriseTrackingSpeed = 100;
  static final private Action[] Actions = new Action[] { CreateAction.DontMove, CreateAction.SpinLeft,
      CreateAction.SpinRight, CreateAction.Forward };
  static final private String[] PredictedLabels = new String[] { "WheelDrop", "Bump", "WheelOverCurrent", "ICOmni",
      "DriveDistance", "DriveAngle", "BatteryCurrent", "BatteryCharge", "WallSignal", "CliffSignal",
      "ConnectedHomeBase", "OIMode", "WheelRequested" };
  static final private double[] Gammas = new double[] { .0, 0.9, 0.99 };
  static final private Policy[] TargetPolicies = new Policy[] { new ActionPolicy(CreateAction.SpinLeft),
      new ActionPolicy(CreateAction.Forward) };
  static final private double Lambda = .7;
  final private IRobotEnvironment robot = new CreateRobot();
  final private Clock clock = new Clock("Surprise");
  final private Horde horde;
  final private Surprise surprise;
  private final AgentState agentState;
  private final Policy robotBehaviour;
  private RealVector x_t;
  private Action a_t;

  public CreateSurprise() {
    agentState = new RobotState();
    robotBehaviour = new RobotBehaviour(new Random(0), .25, Actions);
    horde = createHorde();
    surprise = new Surprise(horde.demons(), SurpriseTrackingSpeed);
    Zephyr.advertise(clock, this);
  }

  private Horde createHorde() {
    List<RewardFunction> rewardFunctions = createRewardFunctions();
    List<Demon> demons = new ArrayList<Demon>();
    for (RewardFunction rewardFunction : rewardFunctions) {
      for (double gamma : Gammas) {
        demons.add(newNextingPredictionDemon(rewardFunction, gamma));
        for (Policy targetPolicy : TargetPolicies)
          demons.add(newOffPolicyPredictionDemon(rewardFunction, gamma, targetPolicy));
      }
    }
    return new Horde(demons, rewardFunctions, null, null);
  }

  private PredictionOffPolicyDemon newOffPolicyPredictionDemon(RewardFunction rewardFunction, double gamma,
      Policy targetPolicy) {
    GTDLambda gtd = new GTDLambda(Lambda, gamma, .1 / agentState.stateNorm(), 0.0001 / agentState.stateNorm(),
                                  agentState.stateSize());
    return new PredictionOffPolicyDemon(targetPolicy, robotBehaviour, gtd, rewardFunction);
  }

  private PredictionDemon newNextingPredictionDemon(RewardFunction rewardFunction, double gamma) {
    OnPolicyTD td = new TDLambda(Lambda, gamma, .1 / agentState.stateNorm(), agentState.stateSize());
    return new PredictionDemon(rewardFunction, td);
  }

  private List<RewardFunction> createRewardFunctions() {
    ArrayList<RewardFunction> rewardFunctions = new ArrayList<RewardFunction>();
    Legend legend = robot.legend();
    ObsFilter filter = new ObsFilter(legend, PredictedLabels);
    for (String label : filter.legend().getLabels())
      rewardFunctions.add(new RewardObservationFunction(legend, label));
    return rewardFunctions;
  }

  @Override
  public void run() {
    robot.fullMode();
    while (clock.tick()) {
      Observation o_tp1 = robot.waitNewRawObs();
      RealVector x_tp1 = agentState.update(a_t, o_tp1);
      horde.update(o_tp1, x_t, a_t, x_tp1);
      double surpriseMeasure = surprise.updateSurpriseMeasure();
      if (surpriseMeasure > 8.0)
        robot.playSong(IRobotSongs.composeHappySong());
      Action a_tp1 = robotBehaviour.decide(x_tp1);
      robot.sendAction((CreateAction) a_tp1);
      x_t = x_tp1;
      a_t = a_tp1;
    }
  }
}
