package rlpark.plugin.rltoys.utils.tilecoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartition;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoder;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Identity;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Tiling;
import rlpark.plugin.rltoys.math.ranges.Range;

public class TileCodersInspectors {
  public TileCoders tc;
  public Range[] ranges;
  public String[] inputNames;
  private double[][] bounds;
  int MIN = 0, MAX = 1, WIDTH = 2, RES = 3, INDEX = 4;

  public TileCodersInspectors(TileCoders tc, Range[] ranges) {
    this(tc, ranges, null);
  }

  public List<Tiling> getTilings() {
    List<Tiling> list = new ArrayList<Tiling>();
    for (TileCoder tt : tc.tileCoders()) {
      for (Tiling t : tt.tilings()) {
        list.add(t);
      }
    }
    return list;
  }

  public double[] getAllTileBoundaries(List<Tiling> tilings, int inputIndex, Double min, Double max) {
    HashSet<Double> breaks = new HashSet<Double>();
    for (Tiling t : tilings) {
      AbstractPartition q;
      int val = 0;
      for (int j : t.inputIndexes()) {
        if (j == inputIndex) {
          q = (AbstractPartition) t.discretizers()[val];
          for (int i = 0; i <= q.resolution; i++)
            breaks.add(q.min + i * q.intervalWidth);
        }
        val++;
      }
    }
    double[] ds = new double[breaks.size()];
    int i = 0;
    for (double d : breaks) {
      if (min != null && d < min)
        continue;
      if (min != null && d > max)
        continue;
      ds[i++] = d;
    }
    Arrays.sort(ds);
    return ds;
  }

  public void printIntegerTileBoundaries(double[] breaks) {
    int b = breaks.length;
    if (b <= 0)
      return;
    int[] vals = new int[b];
    for (int i = 0; i < b; i++)
      vals[i] = (int) breaks[i];
    Arrays.sort(vals);
    int maxgap = -1;
    int mingap = vals[b - 1] - vals[0];
    System.out.print(" Checking integer coverage \n breaks: ");
    int last = vals[0];
    int histogram[] = new int[vals[b - 1] - vals[0]];
    for (int i = 1; i < b; i++) {
      if (i == 0 || vals[i] != last) {
        System.out.print(vals[i] + " ");
        int delta = vals[i] - last;
        maxgap = delta > maxgap ? delta : maxgap;
        mingap = delta < mingap ? delta : mingap;
        last = vals[i];
        if (delta < histogram.length - 1)
          histogram[delta] += 1;
        else
          histogram[histogram.length - 1] += 1;
      }
    }
    System.out.println();
    System.out.print(" Gaps: max: " + maxgap + " min: " + mingap + "\n Histogram: ");

    for (int i = 0; i < histogram.length; i++) {
      if (histogram[i] > 0)
        System.out.print(" (gap=" + i + (i == histogram.length - 1 ? "+" : "") + " count=" + histogram[i] + ")");
    }
    System.out.println();
  }

  public void examineAllIntegerTileBoundaries() {
    List<Tiling> q = getTilings();
    for (int i = 0; i < ranges.length; i++) {
      double[] tileBoundaries = getAllTileBoundaries(q, i, null, null);
      System.out.println("Index: " + i + " " + identifier(i));
      printIntegerTileBoundaries(tileBoundaries);
    }
  }

  public TileCodersInspectors(TileCoders tc, Range[] ranges, String[] inputNames) {
    this.tc = tc;
    this.inputNames = inputNames;
    this.ranges = ranges;
    // this.ranges = ((AbstractPartitionFactory) tc.discretizerFactory())
    // .rangesClone();
  }

  public String description(boolean full) {
    String output = "";
    String tileDescriptions = "";
    output += "Inputs: " + tc.nbInputs();
    output += " Tilings: " + tc.vectorNorm();
    output += " Tiles: " + tc.vectorSize();
    output += " NumTileCoders: " + tc.tileCoders().size() + "\n";
    for (int i = 0; i < ranges.length; i++) {
      String name = " " + (inputNames != null && i < inputNames.length ? inputNames[i] : "");
      output += "Input: " + i + " " + ranges[i].min() + " " + ranges[i].max() + name + "\n";
    }
    output += "\n";
    int numTilings = 0;
    int netTiles = 0;
    for (int i = 0; i < tc.tileCoders().size(); i++) {
      TileCoder tileCoder = tc.tileCoders().get(i);
      int n = tileCoder.tilings().length;
      output += "TileCoders: " + i + " " + "Size: " + n + "\n";
      for (int j = 0; j < tileCoder.tilings().length; j++) {
        output += describeTiling(i, j, tileCoder.tilings()[j], numTilings, netTiles);
        tileDescriptions += describeTiles(i, j, tileCoder.tilings()[j], numTilings, netTiles);
        netTiles += new Identity(tileCoder.tilings()[j]).memorySize();
        numTilings++;
      }
      output += "\n";
    }
    if (full)
      return output + "\n\n" + tileDescriptions;
    return output;
  }

  public static String str(double d) {
    return String.format("%4.3f", d);
  }

  private static String strDv(double[] inputTop) {
    String out = "";
    for (double d : inputTop)
      out += " " + str(d);
    return "(" + out + ")";
  }

  private static String strIv(int[] qTop) {
    String out = "";
    for (int d : qTop)
      out += " " + d;
    return "(" + out + ")";
  }

  private String describeTiles(int tcs, int tilingIndex, Tiling tiling, int numTilings, int netTiles) {
    Identity id = new Identity(tiling);
    String tileDescriptions = "";
    double[] inputTop = new double[ranges.length];
    double[] inputBot = new double[ranges.length];
    double[] top = new double[bounds.length];
    double[] bot = new double[bounds.length];

    int[] multipliers = new int[bounds.length + 1];
    multipliers[0] = 1;
    for (int i = 1; i <= bounds.length; i++)
      multipliers[i] = (int) (multipliers[i - 1] * bounds[i - 1][RES]);

    int[] inputs = new int[bounds.length];
    for (int a = 0; a < bounds.length; a++)
      inputs[a] = (int) bounds[a][INDEX];
    double eps = 0.001;
    for (int i = 0; i < id.memorySize(); i++) {
      for (int j = 0; j < bounds.length; j++) {
        bot[j] = bounds[j][MIN] + ((i % multipliers[j + 1]) / multipliers[j]) * bounds[j][WIDTH];
        top[j] = bounds[j][MIN] + (1 + ((i % multipliers[j + 1]) / multipliers[j])) * bounds[j][WIDTH] - eps;
        inputBot[(int) bounds[j][INDEX]] = bot[j];
        inputTop[(int) bounds[j][INDEX]] = top[j];
      }
      int name = netTiles + i;
      int[] qTop = tiling.tilesCoordinates(inputTop);
      int[] qBot = tiling.tilesCoordinates(inputTop);
      int pt = id.hash(tiling, qTop);
      int pb = id.hash(tiling, qBot);
      tileDescriptions += " Tile: " + name + " Tiling: " + numTilings + " index: " + i + " =? " + pt + " =? " + pb
          + " coords: " + strIv(qBot) + " bounds: ";
      for (int kk = 0; kk < bounds.length; kk++) {
        String ident = identifier(inputs[kk]);
        tileDescriptions += " [ " + bot[kk] + " <= " + ident + " < " + top[kk] + "]";
      }
      tileDescriptions += "\n";
    }
    return tileDescriptions;
  }

  public String identifier(int val) {
    return inputNames != null && val < inputNames.length ? inputNames[val] : "input" + val;
  }

  private String describeTiling(int tcs, int tilingIndex, Tiling tiling, int numTilings, int netTiles) {
    Identity id = new Identity(tiling);
    String output = "";
    output += "Tiling: " + numTilings + " TileCodersGroup: " + tcs + " " + "index: " + tilingIndex + " size: "
        + id.memorySize() + " inputs :";
    for (int i : tiling.inputIndexes()) {
      output += " " + i + " (" + identifier(i) + ")";
    }
    output += "\n";
    int inputIndex = 0;
    int netSize = 1;
    bounds = new double[tiling.discretizers().length][5];
    for (Discretizer discretizer : tiling.discretizers()) {
      AbstractPartition partition = (AbstractPartition) discretizer;
      output += "Tiling: " + numTilings + " dimension: " + inputIndex + " input: " + tiling.inputIndexes()[inputIndex]
          + " min=" + partition.min + " max=" + partition.max + " width=" + partition.intervalWidth + " resolution="
          + partition.resolution;
      bounds[inputIndex][MIN] = partition.min;
      bounds[inputIndex][MAX] = partition.max;
      bounds[inputIndex][RES] = partition.resolution;
      bounds[inputIndex][WIDTH] = partition.intervalWidth;
      bounds[inputIndex][INDEX] = tiling.inputIndexes()[inputIndex];
      output += " Divisions: ";
      netSize *= partition.resolution;
      for (int i = 0; i < partition.resolution; i++) {
        double v = partition.min + i * partition.intervalWidth;
        output += str(v) + " ";
      }
      output += str(partition.max) + "\n";
      inputIndex++;
    }
    return output;
  }


}