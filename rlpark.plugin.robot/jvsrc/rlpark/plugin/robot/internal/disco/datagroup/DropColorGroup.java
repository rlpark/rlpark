package rlpark.plugin.robot.internal.disco.datagroup;

import java.awt.Color;

import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropColor;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;

public class DropColorGroup extends DataObjectGroup<Color> {

  public DropColorGroup(Drop drop) {
    super(drop);
  }

  @Override
  protected boolean isDataSelected(DropData data) {
    return data instanceof DropColor;
  }

  @Override
  protected void setValue(DropData dropData, Color value) {
    ((DropColor) dropData).set(value);
  }

  @Override
  protected Color getValue(LiteByteBuffer byteBuffer, DropData dropData) {
    return ((DropColor) dropData).color(byteBuffer);
  }
}