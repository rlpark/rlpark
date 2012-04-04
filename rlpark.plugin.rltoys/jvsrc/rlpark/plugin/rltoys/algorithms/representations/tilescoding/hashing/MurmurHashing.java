package rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing;

import java.util.Random;

public class MurmurHashing extends AbstractHashing {
  private static final long serialVersionUID = -7644303591039542570L;
  private final int seed;

  public MurmurHashing(Random random, int memorySize) {
    super(memorySize);
    seed = random.nextInt();
  }

  @Override
  protected int hash(int[] coordinates) {
    byte[] data = new byte[coordinates.length];
    coordinates[0] = (coordinates[0] - Byte.MIN_VALUE) % (Byte.MAX_VALUE - Byte.MIN_VALUE) + Byte.MIN_VALUE;
    for (int i = 0; i < data.length; i++) {
      assert coordinates[i] >= Byte.MIN_VALUE && coordinates[i] <= Byte.MAX_VALUE;
      data[i] = (byte) coordinates[i];
    }
    return (int) (((long) MurmurHash2.hash(data, seed) + Integer.MAX_VALUE) % memorySize);
  }

}
