package rlpark.plugin.rltoys.experiments.scheduling.interfaces;

import java.util.concurrent.Semaphore;

public class PoolResult {
  private final Semaphore semaphore = new Semaphore(0);
  private boolean poolDone = false;

  public void poolDone() {
    poolDone = true;
    semaphore.release();
  }

  public void waitPool() {
    if (poolDone)
      return;
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
