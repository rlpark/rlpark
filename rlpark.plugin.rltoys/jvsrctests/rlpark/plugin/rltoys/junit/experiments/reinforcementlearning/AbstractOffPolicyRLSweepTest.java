package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

public abstract class AbstractOffPolicyRLSweepTest extends RLSweepTest {
  private final Map<String, Double> behaviourPerformance = new HashMap<String, Double>();
  private boolean behaviourPerformanceChecked = false;

  protected void checkBehaviourPerformanceValue(String filename, String label, double value) {
    if (filename == null)
      return;
    String key = filename + label;
    if (!behaviourPerformance.containsKey(key)) {
      behaviourPerformance.put(key, value);
      return;
    }
    Assert.assertEquals(5.0, behaviourPerformance.get(key), 0.1);
    behaviourPerformanceChecked = true;
  }

  protected boolean isBehaviourPerformanceChecked() {
    return behaviourPerformanceChecked;
  }
}
