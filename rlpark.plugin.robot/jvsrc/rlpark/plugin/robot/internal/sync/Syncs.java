package rlpark.plugin.robot.internal.sync;

import rlpark.plugin.robot.observations.ObservationVersatile;

public class Syncs {
  static public ObservationVersatile createObservation(long time, LiteByteBuffer buffer, ScalarInterpreter interpreter) {
    double[] doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
    return new ObservationVersatile(time, buffer.array().clone(), doubleValues);
  }
}
