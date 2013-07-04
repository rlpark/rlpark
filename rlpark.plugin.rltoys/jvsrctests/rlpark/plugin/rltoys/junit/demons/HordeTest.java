package rlpark.plugin.rltoys.junit.demons;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.horde.Horde;
import rlpark.plugin.rltoys.horde.demons.Demon;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;

public class HordeTest {
  protected static final String JUnitFolder = ".junittests_horde";

  static class TestDemon implements Demon {
    private static final long serialVersionUID = -8079627439006250307L;
    int nbUpdate = 0;

    @Override
    public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
      nbUpdate++;
    }

    @Override
    public LinearLearner learner() {
      return null;
    }
  }

  @Before
  public void before() throws IOException {
    File folder = new File(JUnitFolder);
    FileUtils.deleteDirectory(folder);
    folder.mkdirs();
  }

  @After
  public void after() throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
  }

  @Test
  public void testSerialization() {
    Horde horde = new Horde();
    horde.addDemon(new TestDemon());
    horde.update(null, null, null, null);
    horde.update(null, null, null, null);
    final String filepath = JUnitFolder + "/horde.bin";
    Utils.save(horde, filepath);
    horde = null;
    horde = (Horde) Utils.load(filepath);
    horde.update(null, null, null, null);
    horde.update(null, null, null, null);
    Assert.assertEquals(4, ((TestDemon) horde.demons().get(0)).nbUpdate);
  }

}
