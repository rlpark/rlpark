package rlpark.example.demos.scheduling;

import java.io.Serializable;

@SuppressWarnings("serial")
class Job implements Runnable, Serializable {
  static int nbJob = 0;
  final public int jobId;
  private int result;

  public Job() {
    jobId = Job.nbJob;
    Job.nbJob++;
  }

  @Override
  public void run() {
    System.out.println("Running job: " + jobId);
    result = -jobId;
  }

  public int result() {
    return result;
  }
}