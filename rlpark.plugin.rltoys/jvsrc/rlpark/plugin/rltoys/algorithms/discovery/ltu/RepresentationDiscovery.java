package rlpark.plugin.rltoys.algorithms.discovery.ltu;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.discovery.sorting.WeightSorter;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.RandomNetworks;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Signal;

public class RepresentationDiscovery implements Serializable {
  private static final long serialVersionUID = 8579686361420622461L;
  public final Signal<LTU> onLTUAdded = new Signal<LTU>();
  private final RandomNetwork network;
  @Monitor
  private final WeightSorter sorter;
  private final LinkedList<Integer> protectedUnits = new LinkedList<Integer>();
  private final int nbProtectedUnits;
  private final LTU prototype;
  private final Random random;
  private int worstUnit;
  private final int nbInputForUnit;

  public RepresentationDiscovery(Random random, RandomNetwork network, WeightSorter sorter, LTU prototype,
      int nbProtectedUnit, int nbInputForUnit) {
    this.random = random;
    this.network = network;
    this.sorter = sorter;
    this.prototype = prototype;
    this.nbInputForUnit = nbInputForUnit;
    this.nbProtectedUnits = nbProtectedUnit;
    assert nbProtectedUnits > 0;
  }

  public void changeRepresentation(int nbUnitsToChange) {
    sorter.sort();
    for (int unitIndex = 0; unitIndex < nbUnitsToChange; unitIndex++) {
      worstUnit = findWorstUnit();
      assert worstUnit >= 0;
      LTU ltu = createNewUnit(worstUnit);
      network.addLTU(ltu);
      sorter.resetWeights(ltu.index());
      addIntoProtectedUnits(worstUnit);
      onLTUAdded.fire(ltu);
    }
  }

  private void addIntoProtectedUnits(int worstUnit) {
    protectedUnits.push(worstUnit);
    if (protectedUnits.size() > nbProtectedUnits)
      protectedUnits.pollLast();
  }

  private LTU createNewUnit(int ltuIndex) {
    return RandomNetworks.newRandomUnit(random, prototype, ltuIndex, network.inputSize, nbInputForUnit);
  }

  protected int findWorstUnit() {
    int worstUnit = -1;
    do {
      worstUnit = sorter.nextWorst();
    } while (protectedUnits.contains(worstUnit));
    return worstUnit;
  }

  public void fillNetwork() {
    RandomNetworks.connect(random, network, prototype, nbInputForUnit, sorter.startSortingPosition(),
                           sorter.endSortingPosition());
  }
}
