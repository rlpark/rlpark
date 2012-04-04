package rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing;

public abstract class AbstractHashing implements Hashing {
  private static final long serialVersionUID = 4766575749242513533L;
  protected final int memorySize;

  public AbstractHashing(int memorySize) {
    this.memorySize = memorySize;
  }

  @Override
  public int memorySize() {
    return memorySize;
  }

  @Override
  public int hash(Tiling tiling, int[] inputs) {
    return hash(toCoordinates(tiling, inputs));
  }

  static protected int[] toCoordinates(Tiling tiling, int[] inputs) {
    int[] coordinates = new int[inputs.length + 1];
    coordinates[0] = tiling.hashingIndex;
    System.arraycopy(inputs, 0, coordinates, 1, inputs.length);
    return coordinates;
  }

  abstract protected int hash(int[] coordinates);
}
