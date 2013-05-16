package rlpark.plugin.rltoys.experiments.helpers;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExperimentCounter implements Cloneable, Serializable {
  private static final long serialVersionUID = 4512747323143545399L;
  public final File folder;
  private static final String LOGEXTENSION = "logtxt";
  public static final String DefaultFileName = "data";
  private final DecimalFormat formatter = new DecimalFormat("00");
  private final int counterMax;
  private int counter = -1;
  private final List<File> folderPrepared = new ArrayList<File>();
  private String defaultName = DefaultFileName;

  public ExperimentCounter(int nbExperiment, String folderPath) {
    counterMax = nbExperiment - 1;
    folder = prepareFolder(folderPath);
  }

  private ExperimentCounter(int nbExperiment, String folderPath, int counter) {
    counterMax = nbExperiment - 1;
    folder = prepareFolder(folderPath);
    this.counter = counter;
  }

  public void setDefaultName(String defaultName) {
    this.defaultName = defaultName;
  }

  private File prepareFolder(String folderPath) {
    File folder = new File(folderPath);
    if (folderPrepared.contains(folder))
      return folder;
    folder.mkdir();
    folderPrepared.add(folder);
    return folder;
  }

  public void reset() {
    counter = -1;
  }

  public boolean hasNext() {
    return counter < counterMax;
  }

  public ExperimentCounter nextExperiment() {
    counter += 1;
    return this;
  }

  public String filename() {
    return filename(defaultName, LOGEXTENSION);
  }

  public String filename(String baseName) {
    return filename(baseName, LOGEXTENSION);
  }

  public String filename(String baseName, String extension) {
    return folderFilename(null, baseName, extension);
  }

  public String folderFilename(String folderName) {
    return folderFilename(folderName, defaultName);
  }

  public String folderFilename(String folderName, String baseName) {
    return folderFilename(folderName, baseName, LOGEXTENSION);
  }

  public String folderFilename(String folderName, String baseName, String extension) {
    String folderPath = folder.getAbsolutePath();
    if (folderName != null && !folderName.isEmpty())
      folderPath = folderPath + "/" + folderName;
    prepareFolder(folderPath);
    return folderPath + "/" + baseName + formatter.format(counter) + "." + extension;
  }

  public Random newRandom() {
    return newRandom(counter);
  }

  public int currentIndex() {
    return counter;
  }

  public static Random newRandom(int counter) {
    return new Random(counter);
  }

  @Override
  public ExperimentCounter clone() {
    ExperimentCounter experimentCounter = new ExperimentCounter(-1, folder.getAbsolutePath(), counter);
    experimentCounter.setDefaultName(defaultName);
    return experimentCounter;
  }
}
