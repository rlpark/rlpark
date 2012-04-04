package rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing;

import java.util.Random;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ColisionDetection implements Hashing {
  private static final long serialVersionUID = -3173836756531431009L;
  private final int[] memoryToHashing;
  @Monitor
  private int nbCollisions;
  private final Hashing hashing;
  private final UNH referenceHashing = new UNH(new Random(-1), Integer.MAX_VALUE / 4);

  public ColisionDetection(Hashing hashing) {
    this.hashing = hashing;
    memoryToHashing = new int[hashing.memorySize()];
  }

  @Override
  public int hash(Tiling tiling, int[] inputs) {
    int result = hashing.hash(tiling, inputs);
    int[] coordinates = AbstractHashing.toCoordinates(tiling, inputs);
    int hash = referenceHashing.hash(coordinates);
    if (memoryToHashing[result] != 0 && memoryToHashing[result] != hash)
      nbCollisions++;
    memoryToHashing[result] = hash;
    return result;
  }

  public int nbCollisions() {
    return nbCollisions;
  }

  @Override
  public int memorySize() {
    return hashing.memorySize();
  }
}
