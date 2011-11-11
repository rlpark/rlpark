package rltoys.algorithms.learning.control.mountaincar;

import java.io.File;

import junit.framework.Assert;
import rltoys.agents.AgentFA;
import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.signals.Listener;


public abstract class MountainCarOnPolicyTest {
  private class PerformanceVerifier implements Listener<RunnerEvent> {
    @Override
    public void listen(RunnerEvent eventInfo) {
      if (eventInfo.episode < 200)
        return;
      Assert.assertTrue(eventInfo.episodeReward > -300);
    }
  }

  protected interface MountainCarControlFactory {
    Control createControl(MountainCar mountainCar, Projector projector);
  };

  protected interface TileCodersFactory {
    TileCoders create(Range[] ranges);
  }

  private final TileCodersFactory defaultTileCodersFactory = new TileCodersFactory() {
    @Override
    public TileCoders create(Range[] ranges) {
      return new TileCodersNoHashing(ranges);
    }
  };

  public void runTestOnOnMountainCar(MountainCarControlFactory controlFactory) {
    runTestOnOnMountainCar(defaultTileCodersFactory, controlFactory);
  }

  @SuppressWarnings("synthetic-access")
  public void runTestOnOnMountainCar(TileCodersFactory tileCodersFactory, MountainCarControlFactory controlFactory) {
    MountainCar mountainCar = new MountainCar(null);
    final int nbEpisode = 300;
    TileCoders tilesCoder = tileCodersFactory.create(mountainCar.getObservationRanges());
    tilesCoder.addFullTilings(9, 10);
    Control control = controlFactory.createControl(mountainCar, tilesCoder);
    AgentFA agent = new AgentFA(control, tilesCoder);
    Runner runner = new Runner(mountainCar, agent, nbEpisode, 5000);
    runner.onEpisodeEnd.connect(new PerformanceVerifier());
    runner.run();
    File tempFile = Utils.createTempFile("junit");
    Utils.save(agent, tempFile);
    Utils.load(tempFile);
  }
}