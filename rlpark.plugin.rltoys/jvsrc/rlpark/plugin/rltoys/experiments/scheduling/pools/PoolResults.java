package rlpark.plugin.rltoys.experiments.scheduling.pools;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.PoolResult;

public class PoolResults {
  private final List<PoolResult> results = new ArrayList<PoolResult>();

  public void add(PoolResult result) {
    results.add(result);
  }

  public void waitPools() {
    for (PoolResult poolResult : results)
      poolResult.waitPool();
    results.clear();
  }
}
