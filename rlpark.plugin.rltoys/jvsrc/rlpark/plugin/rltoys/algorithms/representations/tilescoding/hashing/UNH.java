package rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing;

import java.util.Random;


/*
 External documentation and recommendations on the use of this code is
 available at http://webdocs.cs.ualberta.ca/~sutton/tiles2.html

 This is an implementation of grid-style tile codings, based originally on 
 the UNH CMAC code (see http://www.ece.unh.edu/robots/cmac.htm). 
 We assume that hashing collisions are to be ignored. There may be
 duplicates in the list of tiles, but this is unlikely if memory-size is
 large. 
 */
public class UNH extends AbstractHashing {
  private static final long serialVersionUID = 6445159636778781514L;
  private static final int increment = 470;
  private final int[] rndseq;

  public UNH(Random random, int memorySize) {
    super(memorySize);
    rndseq = new int[16384];
    for (int k = 0; k < rndseq.length; k++) {
      rndseq[k] = 0;
      for (int i = 0; i < 4; ++i)
        rndseq[k] = rndseq[k] << 8 | random.nextInt() & 0xff;
    }
  }

  @Override
  protected int hash(int[] coordinates) {
    int index = 0;
    long sum = 0;
    for (int i = 0; i < coordinates.length; i++) {
      /* add random table offset for this dimension and wrap around */
      index = coordinates[i];
      index += increment * i;
      index %= rndseq.length;
      while (index < 0)
        index += rndseq.length;
      /* add selected random number to sum */
      sum += rndseq[index];
    }
    index = (int) (sum % memorySize);
    while (index < 0)
      index += memorySize;
    return index;
  }
}
