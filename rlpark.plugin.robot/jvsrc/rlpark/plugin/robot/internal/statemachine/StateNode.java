package rlpark.plugin.robot.internal.statemachine;


public interface StateNode<T> {
  void start();

  void step(T step);

  boolean isDone();
}
