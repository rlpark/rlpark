package rlpark.plugin.rltoys.junit.algorithms.control.mountaincar;

import java.io.File;
import java.util.Random;

import org.junit.Assert;

import rlpark.plugin.rltoys.agents.representations.ProjectorFactory;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.RLProblem;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.signals.Listener;

@SuppressWarnings("serial")
public abstract class MountainCarOnPolicyTest {
  private class PerformanceVerifier implements Listener<AbstractRunner.RunnerEvent> {
    @Override
    public void listen(AbstractRunner.RunnerEvent eventInfo) {
      if (eventInfo.nbEpisodeDone < 200)
        return;
      final double minEpisodeReward = -350;
      if (eventInfo.episodeReward < minEpisodeReward) {
        Assert.fail("Episode reward too low (=" + eventInfo.episodeReward + ")");
      }
    }
  }

  protected interface MountainCarAgentFactory {
    RLAgent createAgent(MountainCar mountainCar, Projector projector);
  };

  private final ProjectorFactory defaultTileCodersFactory = new ProjectorFactory() {
    @Override
    public Projector createProjector(long seed, RLProblem problem) {
      TileCodersNoHashing projector = new TileCodersNoHashing(((ProblemBounded) problem).getObservationRanges());
      projector.addFullTilings(9, 10);
      return projector;
    }
  };

  public final static ProjectorFactory hashingTileCodersFactory = new ProjectorFactory() {
    @Override
    public Projector createProjector(long seed, RLProblem problem) {
      Random random = new Random(seed);
      Range[] ranges = ((ProblemBounded) problem).getObservationRanges();
      AbstractPartitionFactory discretizerFactory = new WrappedPartitionFactory(ranges);
      discretizerFactory.setRandom(random, 0.1);
      TileCoders tileCoders = new TileCodersHashing(new UNH(random, 10000), discretizerFactory, ranges.length);
      tileCoders.addFullTilings(9, 10);
      return tileCoders;
    }
  };

  public void runTestOnOnMountainCar(MountainCarAgentFactory controlFactory) {
    runTestOnOnMountainCar(defaultTileCodersFactory, controlFactory);
  }

  @SuppressWarnings("synthetic-access")
  public void runTestOnOnMountainCar(ProjectorFactory projectorFactory, MountainCarAgentFactory agentFactory) {
    MountainCar mountainCar = new MountainCar(null);
    final int nbEpisode = 300;
    Projector projector = projectorFactory.createProjector(0, mountainCar);
    RLAgent agent = agentFactory.createAgent(mountainCar, projector);
    Runner runner = new Runner(mountainCar, agent, nbEpisode, 5000);
    runner.onEpisodeEnd.connect(new PerformanceVerifier());
    runner.run();
    File tempFile = Utils.createTempFile("junit");
    Utils.save(agent, tempFile);
    Utils.load(tempFile);
  }
}