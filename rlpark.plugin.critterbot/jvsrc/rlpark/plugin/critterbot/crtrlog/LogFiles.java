package rlpark.plugin.critterbot.crtrlog;

import java.io.File;

import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.utils.Utils;

public class LogFiles {
  static private int nbSamples;
  static private int nbSkipSamples;

  public static Range[] extractRanges(CrtrLogFile logFile, Range[] ranges, boolean filterSamples) {
    int errorFlagIndex = filterSamples ? logFile.legend().indexOf(CritterbotLabels.ErrorFlags) : -1;
    nbSamples = 0;
    nbSkipSamples = 0;
    while (logFile.hasNextStep()) {
      double[] o_tp1 = logFile.step();
      if (errorFlagIndex != -1 && o_tp1[errorFlagIndex] != 0 && o_tp1[errorFlagIndex] != 32768) {
        nbSkipSamples++;
        continue;
      }
      for (int i = 0; i < o_tp1.length; i++)
        ranges[i].update(o_tp1[i]);
      nbSamples++;
    }
    logFile.close();
    return ranges;
  }

  static public Range[] extractRanges(String filepath, boolean filterSamples) {
    CrtrLogFile logFile = CrtrLogFile.load(filepath);
    Legend legend = logFile.legend();
    Range[] ranges = new Range[legend.nbLabels()];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(Double.MAX_VALUE, -Double.MAX_VALUE);
    return extractRanges(logFile, ranges, filterSamples);
  }

  public static Range[] extractRanges(CrtrLogFile logFile, boolean filterSamples) {
    return extractRanges(logFile.filepath, filterSamples);
  }

  public static Range[] extractRanges(CrtrLogFile logFile) {
    return extractRanges(logFile.filepath, true);
  }

  public static Range[] extractRanges(String[] files, boolean filterSamples) {
    Range[] ranges = extractRanges(files[0], true);
    int totalNbSamples = nbSamples;
    int totalNbSkipSamples = nbSkipSamples;
    for (int i = 1; i < files.length; i++) {
      CrtrLogFile logFile = CrtrLogFile.load(files[i]);
      extractRanges(logFile, ranges, filterSamples);
      totalNbSamples += nbSamples;
      totalNbSkipSamples += nbSkipSamples;
    }
    nbSamples = totalNbSamples;
    nbSkipSamples = totalNbSkipSamples;
    return ranges;
  }

  public static Range[] loadRange(String rangeFilePath, boolean filterSamples, String... files) {
    File rangeFile = new File(rangeFilePath);
    if (rangeFile.canRead())
      return (Range[]) Utils.load(rangeFile);
    Range[] ranges = extractRanges(files, filterSamples);
    Utils.save(ranges, rangeFile);
    return ranges;
  }
}
