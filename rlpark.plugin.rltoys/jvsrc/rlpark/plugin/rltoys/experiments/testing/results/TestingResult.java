package rlpark.plugin.rltoys.experiments.testing.results;

public class TestingResult<T> {
  public final boolean passed;

  public final String message;

  public final T instance;

  public TestingResult(boolean passed, String message, T instance) {
    this.passed = passed;
    this.message = message;
    this.instance = instance;
  }
}
