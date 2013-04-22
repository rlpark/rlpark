package rlpark.plugin.rltoys.utils.tilecoding;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedPartition;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.WrappedPartition;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoder;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Tiling;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.UNH;

public class TilingGenerator {

  static void addBoundedTiling(TileCoders tc, int[] inputIndexes, double[] mins, double[] maxs, int resolution) {
    Discretizer[] discretizers = new Discretizer[inputIndexes.length];
    for (int i = 0; i < discretizers.length; i++) {
      discretizers[i] = new BoundedPartition(mins[i], maxs[i], resolution);
    }
    Tiling tiling = new Tiling(0, discretizers, inputIndexes);
    tc.tileCoders().add(new TileCoder(new Tiling[] { tiling }, 0));
  }

  static void addWrappingTiling(TileCoders tc, int[] inputIndexes, double[] offsets, double[] widths, int resolution) {
    Discretizer[] discretizers = new Discretizer[inputIndexes.length];
    for (int i = 0; i < discretizers.length; i++) {
      discretizers[i] = new WrappedPartition(offsets[i], offsets[i] + widths[i] * resolution, resolution);
    }
    Tiling tiling = new Tiling(0, discretizers, inputIndexes);
    tc.tileCoders().add(new TileCoder(new Tiling[] { tiling }, 0));
  }


  static void addUNHTiling(TileCoders tc, UNH Hasher, int[] inputIndexes, double[] offsets, double[] widths,
      int resolution) {
    Discretizer[] discretizers = new Discretizer[inputIndexes.length];
    for (int i = 0; i < discretizers.length; i++) {
      discretizers[i] = new WrappedPartition(offsets[i], offsets[i] + widths[i] * resolution, resolution);
    }
    Tiling tiling = new Tiling(0, discretizers, inputIndexes);
    tc.tileCoders().add(new TileCoder(new Tiling[] { tiling }, 0));
  }

  static void addRandomOffsetWrappingTiling(TileCoders tc, int[] inputIndexes, Random random, double[] widths,
      int resolution) {
    /* adds a offset of random*tilewidth to the offset */
    double[] offsets = new double[widths.length];
    for (int i = 0; i < widths.length; i++)
      offsets[i] = random.nextDouble() * widths[i];
    addWrappingTiling(tc, inputIndexes, offsets, widths, resolution);
  }

  static void addRandomNegativeOffsetBoundedTiling(TileCoders tc, int[] inputIndexes, Random random, double[] mins,
      double[] maxs, int resolution) {
    /* adds a negative offset of random*tilewidth to the mins and maxs */
    double[] offsetmaxs = new double[maxs.length];
    double[] offsetmins = new double[mins.length];

    for (int i = 0; i < mins.length; i++) {
      double offset = random.nextDouble() * (maxs[i] - mins[i]) / resolution;
      offsetmins[i] = mins[i] - offset;
      offsetmaxs[i] = maxs[i] - offset;
    }
    addBoundedTiling(tc, inputIndexes, offsetmins, offsetmaxs, resolution);
  }

}
