package rlpark.plugin.rltoys.junit.experiments.parametersweep;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;


public class ParametersTest {
  static private String[] NoFlag = new String[] {};

  @Test
  public void testParametersEquals01NoFlags() {
    FrozenParameters p01 = createParameter01(NoFlag);
    FrozenParameters p01bis = createParameter01Bis(NoFlag);
    Assert.assertTrue(p01.equals(p01bis));
    Assert.assertEquals(p01.hashCode(), p01bis.hashCode());
    Set<FrozenParameters> set = new HashSet<FrozenParameters>();
    set.add(p01);
    set.add(p01bis);
    Assert.assertEquals(1, set.size());
    Assert.assertTrue(set.contains(createParameter01(NoFlag)));
    Assert.assertFalse(set.contains(createParameter02(NoFlag)));
    FrozenParameters p02 = createParameter02(NoFlag);
    Assert.assertFalse(p01.equals(p02));
    Assert.assertTrue(p01.hashCode() != p02.hashCode());
    set.add(p02);
    Assert.assertEquals(2, set.size());
  }

  @Test
  public void testParametersEquals01WithFlags() {
    FrozenParameters p01 = createParameter01(new String[] { "Flag01" });
    FrozenParameters p01bis = createParameter01Bis(new String[] { "Flag01" });
    Assert.assertTrue(p01.equals(p01bis));
    Assert.assertEquals(p01.hashCode(), p01bis.hashCode());
    p01bis = createParameter01Bis(new String[] {});
    p01bis.infos().put("Flag01", 2.0);
    Assert.assertFalse(p01.equals(p01bis));
    Assert.assertEquals(p01.hashCode(), p01bis.hashCode());
    FrozenParameters p02 = createParameter01(new String[] { "Flag01", "Flag02" });
    Assert.assertTrue(p01.equals(p02));
    Assert.assertEquals(p01.hashCode(), p02.hashCode());
  }

  private FrozenParameters toParameters(String[] flags, Object... objects) {
    RunInfo infos = new RunInfo();
    for (String flag : flags)
      infos.enableFlag(flag);
    Parameters parameters = new Parameters(infos);
    for (int i = 0; i < objects.length / 2; i++)
      parameters.putSweepParam((String) objects[i * 2], (double) (Integer) objects[i * 2 + 1]);
    return parameters.froze();
  }

  private FrozenParameters createParameter02(String[] flags) {
    return toParameters(flags, "Hello", 0, "Bye", 3);
  }

  private FrozenParameters createParameter01(String[] flags) {
    return toParameters(flags, "Hello", 0, "Bye", 2);
  }

  private FrozenParameters createParameter01Bis(String[] flags) {
    return toParameters(flags, "Bye", 2, "Hello", 0);
  }
}
