package rlpark.alltests;

import java.io.IOException;

import jdepend.framework.JDepend;
import junit.framework.Assert;

import org.junit.Test;

public class PackageCycleTest {
  static private String[] folders = { "rlpark.plugin.rltoys", "rlpark.plugin.robot", "rlpark.alltests" };

  @Test
  public void testAllPackages() throws IOException {
    if (!projectsHasCycles())
      return;
    JDepend jdepend = new JDepend();
    for (String folder : folders) {
      jdepend.addDirectory(String.format("../%s/bin", folder));
      jdepend.analyze();
      Assert.assertFalse("Cycles exist in " + folder, jdepend.containsCycles());
    }
  }

  private boolean projectsHasCycles() throws IOException {
    JDepend jdepend = new JDepend();
    for (String folder : folders)
      jdepend.addDirectory(String.format("../%s/bin", folder));
    jdepend.analyze();
    return jdepend.containsCycles();
  }
}
