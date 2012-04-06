package rlpark.plugin.robot.internal.disco.datagroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropArray;
import rlpark.plugin.robot.internal.disco.drops.DropData;

public abstract class DataGroup {
  protected final DropData[] dropDatas;
  protected final Legend legend;
  protected final Drop drop;

  public DataGroup(String prefix, Drop drop) {
    dropDatas = getDropData(prefix, drop);
    this.drop = drop;
    legend = getLegend();
  }

  protected Legend getLegend() {
    List<String> labels = new ArrayList<String>();
    for (DropData data : dropDatas) {
      assert !labels.contains(data.label);
      labels.add(data.label);
    }
    return new Legend(labels);
  }

  protected DropData[] getDropData(String prefix, Drop drop) {
    List<DropData> datas = new ArrayList<DropData>();
    addDropData(prefix, datas, drop.dropDatas());
    DropData[] dropDatas = new DropData[datas.size()];
    datas.toArray(dropDatas);
    return dropDatas;
  }

  private void addDropData(String prefix, List<DropData> datas, DropData[] dropDatas) {
    for (DropData data : dropDatas)
      if (data instanceof DropArray)
        addDropData(prefix, datas, ((DropArray) data).dropDatas());
      else if (!data.readOnly && isDataEligible(prefix, data) && isDataSelected(data))
        datas.add(data);
  }

  private boolean isDataEligible(String prefix, DropData data) {
    if (prefix.isEmpty())
      return true;
    return data.label.startsWith(prefix);
  }

  public Legend legend() {
    return legend;
  }

  public Drop drop() {
    return drop;
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj))
      return true;
    return drop().equals(((DataGroup) obj).drop());
  }

  @Override
  public int hashCode() {
    return size();
  }

  public void setTime(long time) {
    drop.setTime(time);
  }

  protected abstract boolean isDataSelected(DropData data);

  public int size() {
    return dropDatas.length;
  }

  @Override
  public String toString() {
    return Arrays.toString(dropDatas);
  }

}