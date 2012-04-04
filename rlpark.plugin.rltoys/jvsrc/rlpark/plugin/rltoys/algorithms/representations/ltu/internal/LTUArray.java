package rlpark.plugin.rltoys.algorithms.representations.ltu.internal;

import java.io.Serializable;
import java.util.Arrays;

import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTU;

public class LTUArray implements Serializable {
  private static final long serialVersionUID = -255407595168336962L;
  private LTU[] array = new LTU[] {};
  private int nbActive = 0;

  public void add(LTU ltu) {
    if (nbActive == array.length) {
      int newLength = array.length > 0 ? array.length * 2 : 1;
      LTU[] previousArray = array;
      array = new LTU[newLength];
      System.arraycopy(previousArray, 0, array, 0, previousArray.length);
    }
    array[nbActive] = ltu;
    nbActive++;
  }

  public void remove(LTU ltu) {
    int index;
    for (index = 0; index < array.length; index++)
      if (array[index] == ltu)
        break;
    System.arraycopy(array, index + 1, array, index, array.length - index - 1);
    nbActive--;
  }

  public LTU[] array() {
    if (array.length > nbActive)
      array = Arrays.copyOf(array, nbActive);
    return array;
  }
}
