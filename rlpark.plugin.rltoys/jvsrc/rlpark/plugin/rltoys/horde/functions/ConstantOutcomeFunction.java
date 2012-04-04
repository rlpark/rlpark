package rlpark.plugin.rltoys.horde.functions;

public class ConstantOutcomeFunction implements OutcomeFunction {
  private final double outcome;

  public ConstantOutcomeFunction(double outcome) {
    this.outcome = outcome;
  }

  @Override
  public double outcome() {
    return outcome;
  }
}
