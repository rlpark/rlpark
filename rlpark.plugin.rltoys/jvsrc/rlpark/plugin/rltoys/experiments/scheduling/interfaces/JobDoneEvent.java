package rlpark.plugin.rltoys.experiments.scheduling.interfaces;

public class JobDoneEvent {
  final public Runnable todo;
  final public Runnable done;

  public JobDoneEvent(Runnable todo, Runnable done) {
    this.todo = todo;
    this.done = done;
  }
}