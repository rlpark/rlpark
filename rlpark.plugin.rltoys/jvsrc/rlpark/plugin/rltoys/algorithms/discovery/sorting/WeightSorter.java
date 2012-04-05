package rlpark.plugin.rltoys.algorithms.discovery.sorting;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class WeightSorter implements Serializable {
  private static final long serialVersionUID = 3375889959423486133L;

  public static class PVectorBasedComparator implements Comparator<Integer>, Serializable {
    private static final long serialVersionUID = -2221092563348361745L;
    final private double[] data;

    public PVectorBasedComparator(PVector reference) {
      data = reference.data;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
      return Double.compare(data[o1], data[o2]);
    }
  };

  @IgnoreMonitor
  private final LinearLearner[] learners;
  @Monitor(level = 4)
  protected final PVector sums;
  @Monitor(level = 4)
  private final Integer[] order;
  private Comparator<Integer> comparator;
  private int worst;
  private final int startSorting;
  private final int endSorting;

  public WeightSorter(LinearLearner[] learners) {
    this(learners, 0, -1);
  }

  public WeightSorter(LinearLearner[] learners, int startSorting, int endSorting) {
    this.learners = learners;
    sums = new PVector(learners[0].weights().size);
    this.startSorting = startSorting;
    this.endSorting = endSorting > 0 ? endSorting : sums.getDimension();
    order = new Integer[this.endSorting - this.startSorting];
    for (int i = this.startSorting; i < this.endSorting; i++)
      order[i - this.startSorting] = i;
  }

  protected Comparator<Integer> createComparator() {
    return new PVectorBasedComparator(sums);
  }

  public void sort() {
    if (comparator == null)
      comparator = createComparator();
    worst = 0;
    updateUnitEvaluation();
    Arrays.sort(order, comparator);
  }

  protected void updateUnitEvaluation() {
    sums.set(0);
    for (LinearLearner learner : learners) {
      double[] weight = learner.weights().data;
      double maxWeight = 0.0;
      for (int i = startSorting; i < endSorting; i++)
        maxWeight = Math.max(Math.abs(weight[i]), maxWeight);
      for (int i = startSorting; i < endSorting; i++)
        sums.data[i] += Math.abs(weight[i]) / maxWeight;
    }
  }

  public boolean hasNext() {
    return worst < order.length;
  }

  public int nextWorst() {
    int result = order[worst];
    worst++;
    return result;
  }

  public void resetWeights(int index) {
    for (LinearLearner learner : learners)
      learner.resetWeight(index);
  }

  public int endSortingPosition() {
    return endSorting;
  }

  public int startSortingPosition() {
    return startSorting;
  }
}