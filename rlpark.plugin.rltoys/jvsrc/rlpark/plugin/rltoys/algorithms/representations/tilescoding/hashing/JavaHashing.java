package rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing;

import java.util.Arrays;


public class JavaHashing extends AbstractHashing {
  private static final long serialVersionUID = 6445159636778781514L;

  public JavaHashing(int memorySize) {
    super(memorySize);
  }

  @Override
  protected int hash(int[] coordinates) {
    return Arrays.hashCode(coordinates) % memorySize;
  }
}
