package rlpark.plugin.rltoys.algorithms.learning.control.mountaincar;

import java.io.File;
import java.util.Random;

import junit.framework.Assert;
import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.projectors.ProjectorFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rlpark.plugin.rltoys.envio.problems.ProblemBounded;
import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.Runner.RunnerEvent;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.signals.Listener;

@SuppressWarnings("serial")
public abstract class MountainCarOnPolicyTest {
  private class PerformanceVerifier implements Listener<RunnerEvent> {
    @Override
    public void listen(RunnerEvent eventInfo) {
      if (eventInfo.episode < 200)
        return;
      Assert.assertTrue(eventInfo.episodeReward > -300);
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
      PartitionFactory discretizerFactory = new PartitionFactory(true, ranges);
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