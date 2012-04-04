package rlpark.plugin.rltoys.algorithms.representations.ltu.networks;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;

public class RandomNetworks {
  public static void fullyConnect(Random random, RandomNetwork randomNetwork, LTU prototype) {
    int outputSize = randomNetwork.outputSize;
    int inputSize = randomNetwork.inputSize;
    for (int i = 0; i < outputSize; i++) {
      byte[] weights = new byte[inputSize];
      int[] inputs = new int[inputSize];
      for (int j = 0; j < inputSize; j++) {
        inputs[j] = j;
        weights[j] = (byte) (random.nextBoolean() ? +1 : -1);
      }
      randomNetwork.addLTU(prototype.newLTU(i, inputs, weights));
    }
  }

  public static void connect(Random random, RandomNetwork randomNetwork, LTU prototype, int nbUnitInputs, int startPos,
      int endPos) {
    int inputSize = randomNetwork.inputSize;
    for (int i = startPos; i < endPos; i++)
      randomNetwork.addLTU(newRandomUnit(random, prototype, i, inputSize, nbUnitInputs));
  }

  public static LTU newRandomUnit(Random random, LTU prototype, int unitIndex, int inputSize, int nbUnitInputs) {
    byte[] weights = new byte[nbUnitInputs];
    int[] inputs = new int[nbUnitInputs];
    boolean[] choosen = new boolean[inputSize];
    for (int j = 0; j < nbUnitInputs; j++) {
      int inputIndex = chooseInput(random, choosen);
      assert !choosen[inputIndex];
      inputs[j] = inputIndex;
      choosen[inputIndex] = true;
      weights[j] = (byte) (random.nextBoolean() ? +1 : -1);
    }
    LTU ltu = prototype.newLTU(unitIndex, inputs, weights);
    return ltu;
  }

  private static int chooseInput(Random random, boolean[] choosen) {
    int result = random.nextInt(choosen.length);
    while (choosen[result])
      result = random.nextInt(choosen.length);
    return result;
  }
}
