package rltoys.experiments.reinforcementlearning;

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
    Assert.assertEquals(behaviourPerformance.get(key), 5.0, 0.0);
    behaviourPerformanceChecked = true;
  }

  protected boolean isBehaviourPerformanceChecked() {
    return behaviourPerformanceChecked;
  }
}
