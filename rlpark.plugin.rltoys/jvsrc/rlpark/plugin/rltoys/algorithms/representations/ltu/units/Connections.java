package rlpark.plugin.rltoys.algorithms.representations.ltu.units;

public class Connections {
  final public int[] indexes;
  final public byte[] weights;
  public int nbActive = 0;

  public Connections(int length) {
    indexes = new int[length];
    weights = new byte[length];
  }

  public void setEntry(int index, byte weight) {
    indexes[nbActive] = index;
    weights[nbActive] = weight;
    nbActive++;
  }

  public double dotProduct(double[] inputVector) {
    double result = 0;
    for (int i = 0; i < indexes.length; i++)
      result += inputVector[indexes[i]] * weights[i];
    return result;
  }
}
