package rlpark.plugin.rltoysview.internal.continuousgridworld;

public class Trajectory {
  private float[][] history = null;
  private int shift;
  private int nbPosition;

  synchronized public void setLength(int length) {
    history = new float[length][];
    shift = 0;
    nbPosition = 0;
  }

  synchronized public void append(double[] position) {
    int index = index(shift);
    history[index] = position != null ? new float[] { (float) position[0], (float) position[1] } : null;
    shift += 1;
    nbPosition++;
  }

  protected int index(int index) {
    int length = history.length;
    return (index + 2 * length) % length;
  }

  synchronized public float[][] getData() {
    final int length = history.length;
    float[][] result = new float[Math.min(nbPosition, length)][];
    if (nbPosition > length) {
      final int shiftIndex = index(shift);
      System.arraycopy(history, shiftIndex, result, 0, length - shiftIndex);
      System.arraycopy(history, 0, result, length - shiftIndex, shiftIndex);
    } else
      System.arraycopy(history, 0, result, 0, shift);
    return result;
  }
}
