package rlpark.plugin.rltoys.experiments.scheduling.interfaces;

public interface Scheduler {
  void start();

  void waitAll();

  JobQueue queue();

  void dispose();
}
