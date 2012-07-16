package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import java.util.Random;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDC;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambdaAutostep;
import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.experiments.testing.predictions.FiniteStateGraphOnPolicy;
import rlpark.plugin.rltoys.experiments.testing.predictions.FiniteStateGraphOnPolicy.OnPolicyTDFactory;
import rlpark.plugin.rltoys.problems.stategraph.LineProblem;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;


public class TDTest {
  private final LineProblem lineProblem = new LineProblem();
  private final RandomWalk randomWalkProblem = new RandomWalk(new Random(0));

  @Test
  public void testTDOnLineProblem() {
    FiniteStateGraphOnPolicy.testTD(lineProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TD(0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDCOnLineProblem() {
    FiniteStateGraphOnPolicy.testTD(lineProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDC(0.9, 0.01, 0.5, nbFeatures);
      }
    });
  }

  @Test
  public void testTDOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TD(0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDLambdaOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambda(0.1, 0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDCOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDC(0.9, 0.01, 0.5, nbFeatures);
      }
    });
  }

  @Test
  public void testGTDLambda0OnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new GTDLambda(0.0, 0.9, 0.01, 0.5, nbFeatures, new AMaxTraces());
      }
    });
  }

  @Test
  public void testGTDLambdaOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new GTDLambda(0.6, 0.9, 0.01, 0.5, nbFeatures, new AMaxTraces());
      }
    });
  }

  @Test
  public void testTDLambdaAutostepOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambdaAutostep(0.1, 0.9, nbFeatures);
      }
    });
  }

  @Test
  public void testTDLambdaAutostepSparseOnRandomWalkProblem() {
    FiniteStateGraphOnPolicy.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambdaAutostep(0.1, 0.9, nbFeatures, new ATraces());
      }
    });
  }
}
