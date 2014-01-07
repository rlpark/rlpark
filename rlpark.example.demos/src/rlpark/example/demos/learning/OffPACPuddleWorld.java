package rlpark.example.demos.learning;

import java.util.Random;

import rlpark.plugin.rltoys.agents.functions.FunctionProjected2D;
import rlpark.plugin.rltoys.agents.functions.ValueFunction2D;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentDirect;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.ActorLambdaOffPolicy;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.ActorOffPolicy;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.CriticAdapterFA;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.OffPAC;
import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.RandomPolicy;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.BoltzmannDistribution;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.TabularActionDiscretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedSmallPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.StateActionCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner.RunnerEvent;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.puddleworld.ConstantFunction;
import rlpark.plugin.rltoys.problems.puddleworld.LocalFeatureSumFunction;
import rlpark.plugin.rltoys.problems.puddleworld.PuddleWorld;
import rlpark.plugin.rltoys.problems.puddleworld.SmoothPuddle;
import rlpark.plugin.rltoys.problems.puddleworld.TargetReachedL1NormTermination;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

@SuppressWarnings("restriction")
@Monitor
public class OffPACPuddleWorld implements Runnable {
  private final Random random = new Random(0);
  private final PuddleWorld behaviourEnvironment = createEnvironment(random);
  private final PuddleWorld evaluationEnvironment = createEnvironment(random);
  private final Runner learningRunner;
  private final Runner evaluationRunner;
  // Visualization with Zephyr
  final FunctionProjected2D valueFunction;
  final Clock clock = new Clock("Off-PAC Demo");
  final Clock episodeClock = new Clock("Episodes");

  public OffPACPuddleWorld() {
    Policy behaviour = new RandomPolicy(random, behaviourEnvironment.actions());
    OffPolicyAgentEvaluable agent = createOffPACAgent(random, behaviourEnvironment, behaviour, .99);
    learningRunner = new Runner(behaviourEnvironment, agent, 5000, -1);
    evaluationRunner = new Runner(evaluationEnvironment, agent.createEvaluatedAgent(), 5000, -1);
    CriticAdapterFA criticAdapter = (CriticAdapterFA) ((OffPolicyAgentDirect) agent).learner().predictor();
    valueFunction = new ValueFunction2D(criticAdapter.projector(), behaviourEnvironment, criticAdapter.predictor());
    connectEpisodesEventsForZephyr();
    Zephyr.advertise(clock, this);
  }

  private void connectEpisodesEventsForZephyr() {
    final double[] episodeInfos = new double[2];
    evaluationRunner.onEpisodeEnd.connect(new Listener<RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        episodeInfos[0] = eventInfo.step.time;
        episodeInfos[1] = eventInfo.episodeReward;
        episodeClock.tick();
        System.out.println(String.format("Episodes %d: %d, %f", eventInfo.nbEpisodeDone, eventInfo.step.time,
                                         eventInfo.episodeReward));
      }
    });
    Zephyr.advertise(episodeClock, new Monitored() {

      @Override
      public double monitoredValue() {
        return episodeInfos[0];
      }
    }, "length");
    Zephyr.advertise(episodeClock, new Monitored() {

      @Override
      public double monitoredValue() {
        return episodeInfos[1];
      }
    }, "reward");
  }

  static private Hashing createHashing(Random random) {
    return new MurmurHashing(random, 1000000);
  }

  static private void setTileCoders(TileCoders projector) {
    projector.addFullTilings(10, 10);
    projector.includeActiveFeature();
  }

  static private AbstractPartitionFactory createPartitionFactory(Random random, Range[] observationRanges) {
    AbstractPartitionFactory partitionFactory = new BoundedSmallPartitionFactory(observationRanges);
    partitionFactory.setRandom(random, .2);
    return partitionFactory;
  }

  static public Projector createProjector(Random random, PuddleWorld problem) {
    final Range[] observationRanges = ((ProblemBounded) problem).getObservationRanges();
    final AbstractPartitionFactory discretizerFactory = createPartitionFactory(random, observationRanges);
    Hashing hashing = createHashing(random);
    TileCodersHashing projector = new TileCodersHashing(hashing, discretizerFactory, observationRanges.length);
    setTileCoders(projector);
    return projector;
  }

  static public StateToStateAction createToStateAction(Random random, PuddleWorld problem) {
    final Range[] observationRanges = problem.getObservationRanges();
    final AbstractPartitionFactory discretizerFactory = createPartitionFactory(random, observationRanges);
    TabularActionDiscretizer actionDiscretizer = new TabularActionDiscretizer(problem.actions());
    Hashing hashing = createHashing(random);
    StateActionCoders stateActionCoders = new StateActionCoders(actionDiscretizer, hashing, discretizerFactory,
                                                                observationRanges.length);
    setTileCoders(stateActionCoders.tileCoders());
    return stateActionCoders;
  }

  private OffPolicyTD createCritic(Projector criticProjector, double gamma) {
    double alpha_v = .1 / criticProjector.vectorNorm();
    GTDLambda gtd = new GTDLambda(.4, gamma, alpha_v, 0, criticProjector.vectorSize(), new ATraces());
    return new CriticAdapterFA(criticProjector, gtd);
  }

  private OffPolicyAgentEvaluable createOffPACAgent(Random random, PuddleWorld problem, Policy behaviour, double gamma) {
    Projector criticProjector = createProjector(random, problem);
    OffPolicyTD critic = createCritic(criticProjector, gamma);
    StateToStateAction toStateAction = createToStateAction(random, problem);
    PolicyDistribution target = new BoltzmannDistribution(random, problem.actions(), toStateAction);
    double alpha_u = .001 / criticProjector.vectorNorm();
    ActorOffPolicy actor = new ActorLambdaOffPolicy(.4, gamma, target, alpha_u, toStateAction.vectorSize(),
                                                    new ATraces());
    return new OffPolicyAgentDirect(behaviour, new OffPAC(behaviour, critic, actor));
  }

  static private PuddleWorld createEnvironment(Random random) {
    PuddleWorld problem = new PuddleWorld(random, 2, new Range(0, 1), new Range(-.05, .05), .1);
    final int[] patternIndexes = new int[] { 0, 1 };
    final double smallStddev = 0.03;
    ContinuousFunction[] features = new ContinuousFunction[] { new ConstantFunction(1),
        new SmoothPuddle(patternIndexes, new double[] { .3, .6 }, new double[] { .1, smallStddev }),
        new SmoothPuddle(patternIndexes, new double[] { .4, .5 }, new double[] { smallStddev, .1 }),
        new SmoothPuddle(patternIndexes, new double[] { .8, .9 }, new double[] { smallStddev, .1 }) };
    final double puddleMalus = -2;
    double[] weights = new double[] { -1, puddleMalus, puddleMalus, puddleMalus };
    problem.setRewardFunction(new LocalFeatureSumFunction(weights, features, 0));
    problem.setTermination(new TargetReachedL1NormTermination(new double[] { 1, 1 }, .1));
    problem.setStart(new double[] { .2, .4 });
    return problem;
  }

  @Override
  public void run() {
    while (clock.tick()) {
      learningRunner.step();
      evaluationRunner.step();
    }
  }

  public static void main(String[] args) {
    new OffPACPuddleWorld().run();
  }
}
