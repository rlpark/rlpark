package rlpark.plugin.rltoys.problems.stategraph02;

import java.io.Serializable;

public class State implements Serializable {
  private static final long serialVersionUID = 1768484355505678751L;
  public final String name;
  public final double reward;

  public State(String name, double reward) {
    this.name = name;
    this.reward = reward;
  }
}
