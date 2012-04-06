package rlpark.plugin.robot.internal.ranges;

import java.util.HashMap;
import java.util.Map;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.datatype.Ranged;
import rlpark.plugin.robot.internal.disco.drops.DropData;

public class RangeProvider {
  private final Map<String, Range> labelToRanges = new HashMap<String, Range>();

  public RangeProvider(DropScalarGroup datas, Map<String, Range> missing) {
    for (DropData drop : datas.drop().dropDatas())
      if (drop instanceof Ranged)
        labelToRanges.put(drop.label, ((Ranged) drop).range());
    if (missing != null)
      labelToRanges.putAll(missing);
  }

  public Range[] ranges(Legend legend) {
    Range[] ranges = new Range[legend.nbLabels()];
    for (int i = 0; i < ranges.length; i++) {
      String label = legend.label(i);
      Range range = labelToRanges.get(label);
      assert range != null;
      ranges[i] = range;
    }
    return ranges;
  }
}
