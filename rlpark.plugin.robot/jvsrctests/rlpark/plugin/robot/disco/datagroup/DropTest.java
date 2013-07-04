package rlpark.plugin.robot.disco.datagroup;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropArray;
import rlpark.plugin.robot.internal.disco.drops.DropColor;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.drops.DropInteger;
import rlpark.plugin.robot.internal.disco.drops.DropTime;


public class DropTest {
  final static DropData[] dropData01 = { new DropInteger("Data1") };
  final static DropData[] dropData02 = { new DropTime(), new DropInteger("Data1") };
  final static DropData[] dropData03 = { new DropInteger("Data1"), new DropInteger("Data2", 9),
      new DropInteger("Data3") };
  final static DropData[] dropData04 = { new DropInteger("Data1"), new DropInteger("Data2"),
      new DropInteger("Data3") };
  final static DropData[] dropData05 = { new DropArray("Bla", 2), new DropInteger("Data"),
      new DropArray("Bl", "i", "o") };
  final static DropData[] dropData06 = { new DropInteger("Bla"), new DropColor("Color") };

  @Test
  public void testDropSize() {
    Assert.assertEquals(1 * 4, new Drop("a", dropData01).dataSize());
    Assert.assertEquals(3 * 4, new Drop("ab", dropData02).dataSize());
    Assert.assertEquals(5 * 4, new Drop("a", dropData05).dataSize());
    Assert.assertEquals(4 + 1 + 4 + 5 * 4, new Drop("a", dropData05).packetSize());
  }

  static protected DropScalarGroup newDrop(DropData[] dropDatas) {
    return newDrop(dropDatas, null);
  }

  static protected DropScalarGroup newDrop(DropData[] dropDatas, double... values) {
    return newDrop("", dropDatas, values);
  }

  static protected DropScalarGroup newDrop(String dropName, DropData[] dropDatas, double... values) {
    Drop drop = new Drop(dropName, dropDatas);
    DropScalarGroup group = new DropScalarGroup(drop);
    if (values != null)
      group.set(values);
    return group;
  }

  @Test
  public void testDropLegend() {
    Legend legend = newDrop(dropData05).legend();
    Assert.assertEquals(0, legend.indexOf("Bla0"));
    Assert.assertEquals(1, legend.indexOf("Bla1"));
    Assert.assertEquals(2, legend.indexOf("Data"));
    Assert.assertEquals(3, legend.indexOf("Bli"));
    Assert.assertEquals(4, legend.indexOf("Blo"));
  }
}
