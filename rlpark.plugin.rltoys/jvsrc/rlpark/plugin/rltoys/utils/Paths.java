package rlpark.plugin.rltoys.utils;

import java.io.File;

public class Paths {
  static String getParkPath() {
    File current = new File("");
    while (!new File(current.getAbsolutePath() + "/.git").canRead())
      current = new File(current.getAbsolutePath() + "/..");
    return current.getAbsolutePath();
  }

  static String getDataPath(String projectFolder) {
    File dataFolder = new File(String.format("%s/%s/data", getParkPath(), projectFolder));
    assert dataFolder.canRead();
    return dataFolder.getAbsolutePath();
  }

  public static String getDataPath(String projectFolder, String filename) {
    String dataFolder = getDataPath(projectFolder);
    File dataFile = new File(dataFolder + "/" + filename);
    assert dataFile.canRead();
    return dataFile.getAbsolutePath();
  }
}
