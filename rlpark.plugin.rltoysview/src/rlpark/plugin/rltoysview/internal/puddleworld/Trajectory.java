package rlpark.plugin.rltoysview.internal.puddleworld;

public class Trajectory {
  final private float[][] history;
  private int shift;
  private int nbPosition;
  private boolean done = false;

  public Trajectory(int maximumLength) {
    history = new float[maximumLength][];
    shift = 0;
    nbPosition = 0;
  }

  public void append(double[] o_t, double[] o_tp1) {
    if (o_tp1 == null)
      return;
    if (nbPosition == 0)
      appendPosition(o_t);
    appendPosition(o_tp1);
  }

  private void appendPosition(double[] position) {
    int index = index(shift);
    history[index] = position != null ? new float[] { (float) position[0], (float) position[1] } : null;
    shift += 1;
    nbPosition++;
  }

  protected int index(int index) {
    int length = history.length;
    return (index + 2 * length) % length;
  }

  public float[][] getData() {
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

  public void endTrajectory() {
    done = true;
  }

  public boolean hasEnded() {
    return done;
  }
}
