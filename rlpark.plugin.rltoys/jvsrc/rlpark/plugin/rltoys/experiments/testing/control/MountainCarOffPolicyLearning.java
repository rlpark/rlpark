package rlpark.plugin.rltoys.experiments.testing.control;

import java.util.Random;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.RandomPolicy;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.TabularActionDiscretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedSmallPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.StateActionCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public class MountainCarOffPolicyLearning {
  static public interface MountainCarEvaluationAgentFactory {
    OffPolicyAgentEvaluable createOffPolicyAgent(Random random, MountainCar problem, Policy behaviour, double gamma);
  }

  static public long evaluate(MountainCarEvaluationAgentFactory agentFactory) {
    return evaluate(agentFactory, 100);
  }

  static public long evaluate(MountainCarEvaluationAgentFactory agentFactory, int nbLearningEpisodes) {
    MountainCar problem = new MountainCar(null);
    Random random = new Random(0);
    Policy behaviour = new RandomPolicy(random, problem.actions());
    OffPolicyAgentEvaluable agent = agentFactory.createOffPolicyAgent(random, problem, behaviour, .99);
    Runner learningRunner = new Runner(problem, agent, 100, 5000);
    learningRunner.run();
    Runner evaluationRunner = new Runner(problem, agent.createEvaluatedAgent(), 1, 5000);
    evaluationRunner.run();
    return evaluationRunner.runnerEvent().step.time;
  }

  private static final int MemorySize = 1000000;

  static private Hashing createHashing(Random random) {
    return new MurmurHashing(random, MemorySize);
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


  static public Projector createProjector(Random random, MountainCar problem) {
    final Range[] observationRanges = ((ProblemBounded) problem).getObservationRanges();
    final AbstractPartitionFactory discretizerFactory = createPartitionFactory(random, observationRanges);
    Hashing hashing = createHashing(random);
    TileCodersHashing projector = new TileCodersHashing(hashing, discretizerFactory, observationRanges.length);
    setTileCoders(projector);
    return projector;
  }

  static public StateToStateAction createToStateAction(Random random, MountainCar problem) {
    final Range[] observationRanges = problem.getObservationRanges();
    final AbstractPartitionFactory discretizerFactory = createPartitionFactory(random, observationRanges);
    TabularActionDiscretizer actionDiscretizer = new TabularActionDiscretizer(problem.actions());
    Hashing hashing = createHashing(random);
    StateActionCoders stateActionCoders = new StateActionCoders(actionDiscretizer, hashing, discretizerFactory,
                                                                observationRanges.length);
    setTileCoders(stateActionCoders.tileCoders());
    return stateActionCoders;
  }
}
