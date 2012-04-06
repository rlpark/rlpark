package rlpark.plugin.robot.internal.sync;


public interface ScalarInterpreter {
  void interpret(LiteByteBuffer buffer, double[] values);

  int size();
}
