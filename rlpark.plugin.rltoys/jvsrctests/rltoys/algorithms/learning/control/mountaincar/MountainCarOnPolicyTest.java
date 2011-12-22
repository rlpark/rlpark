package rltoys.algorithms.learning.control.mountaincar;

import java.io.File;
import java.util.Random;

import junit.framework.Assert;
import rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rltoys.algorithms.representations.projectors.ProjectorFactory;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.envio.problems.ProblemBounded;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;
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