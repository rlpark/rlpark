package rlpark.plugin.rltoys.experiments.representations;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.discovery.ltu.RecursiveWeightSorter;
import rlpark.plugin.rltoys.algorithms.discovery.ltu.RepresentationDiscovery;
import rlpark.plugin.rltoys.algorithms.discovery.ltu.WeightSorter;
import rlpark.plugin.rltoys.algorithms.learning.predictions.LinearLearner;
import rlpark.plugin.rltoys.algorithms.learning.predictions.supervised.Adaline;
import rlpark.plugin.rltoys.algorithms.representations.ltu.StateUpdate;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.AutoRegulatedNetwork;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTUThreshold;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.problems.counting.CountingProblem;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class CountingExperiment {
  protected static final double MinDensity = 0.01;
  protected static final double MaxDensity = 0.05;
  private static final int inputSize = 7; // 11
  private static final int outputSize = 10000;
  private static final int NbLearner = 20;
  private static final int MaxNbUnitInput = 10;
  private final CountingProblem problem;
  private final Adaline[] learners;
  private final double[] errorsSquared;
  private final Clock clock = new Clock("Counting Experiment");
  private final BVector s_t = new BVector(outputSize + inputSize + 1);

  private RepresentationDiscovery discovery = null;
  private final StateUpdate stateUpdate;

  public CountingExperiment(CountingProblem problem, RandomNetwork representation) {
    this.problem = problem;
    stateUpdate = new StateUpdate(representation, inputSize);
    learners = new Adaline[NbLearner];
    errorsSquared = new double[NbLearner];
    for (int i = 0; i < learners.length; i++)
      learners[i] = new Adaline(s_t.size, 0.1 / (s_t.size * MaxDensity));
    Zephyr.advertise(clock, this);
  }

  public void setDiscovery(RepresentationDiscovery discovery) {
    this.discovery = discovery;
  }

  protected LinearLearner[] extractLinearLearners() {
    LinearLearner[] linearLearners = new LinearLearner[learners.length];
    for (int i = 0; i < linearLearners.length; i++)
      linearLearners[i] = learners[i];
    return linearLearners;
  }

  public double[] run() {
    while (clock.tick()) {
      BVector obs = problem.updateInput();
      BVector s_tp1 = stateUpdate.updateState(obs);
      int[] targets = problem.targets();
      for (int i = 0; i < targets.length; i++) {
        double error = learners[i].learn(s_tp1, targets[i]);
        errorsSquared[i] = error * error;
      }
      s_t.clear();
      s_t.mergeSubVector(0, s_tp1);
      if (clock.timeStep() > 100000 && clock.timeStep() % 1000 == 0)
        discovery.changeRepresentation(1);
      if (clock.timeStep() > 500000000)
        break;
    }
    return errorsSquared;
  }

  public static void main(String[] args) {
    Random random = new Random(0);
    CountingProblem problem = new CountingProblem(inputSize, NbLearner);
    RandomNetwork representation = new AutoRegulatedNetwork(random, outputSize + inputSize + 1, outputSize,
                                                             MinDensity, MaxDensity);
    CountingExperiment experiment = new CountingExperiment(problem, representation);
    WeightSorter sorter = new RecursiveWeightSorter(representation, experiment.extractLinearLearners(), NbLearner);
    LTU prototype = new LTUThreshold();
    RepresentationDiscovery discovery = new RepresentationDiscovery(random, representation, sorter, prototype,
                                                                    outputSize / 10, MaxNbUnitInput);
    discovery.fillNetwork();
  }
}
