package rlpark.plugin.rltoys.experiments.parametersweep.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.internal.logfiles.LogFile;

@SuppressWarnings("restriction")
public class ParametersLogFileReader {
  public final String filepath;
  private final RunInfo infos;

  public ParametersLogFileReader(String filepath) {
    this.filepath = filepath;
    infos = readInfos();
  }

  private RunInfo readInfos() {
    String infoFilepath = toInfoFilepath(filepath);
    if (!new File(infoFilepath).canRead())
      return null;
    LogFile logFile = LogFile.load(infoFilepath);
    String[] labels = logFile.labels();
    logFile.step();
    double[] values = logFile.currentLine();
    RunInfo infos = new RunInfo();
    for (int i = 0; i < values.length; i++)
      infos.put(labels[i], values[i]);
    return infos;
  }

  static protected String toInfoFilepath(String filepath) {
    return new File(filepath).getParentFile().getAbsolutePath() + "/infos.txt.gz";
  }

  private String[] readLabels(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line == null)
      return null;
    while (line.startsWith("#") || line.startsWith(" "))
      line = line.substring(1);
    return line.split(" ");
  }

  private FrozenParameters readParameter(Set<String> parameterLabels, String[] labels, BufferedReader reader)
      throws IOException {
    String read = reader.readLine();
    if (read == null)
      return null;
    Map<String, Double> parameterMap = new LinkedHashMap<String, Double>();
    Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
    String[] values = read.split(" ");
    for (int i = 0; i < values.length; i++)
      if (parameterLabels.contains(labels[i]))
        parameterMap.put(labels[i], Double.parseDouble(values[i]));
      else
        resultMap.put(labels[i], Double.parseDouble(values[i]));
    return new FrozenParameters(infos, parameterMap, resultMap);
  }

  public List<FrozenParameters> extractParameters(String... parameterLabelsArray) {
    Set<String> parameterLabels = Utils.asSet(parameterLabelsArray);
    List<FrozenParameters> result = new ArrayList<FrozenParameters>();
    Set<FrozenParameters> alreadyLoaded = new HashSet<FrozenParameters>();
    if (!canRead() || infos == null)
      return result;
    FileInputStream in = null;
    try {
      in = new FileInputStream(filepath);
      InputStreamReader inReader = new InputStreamReader(in);
      BufferedReader reader = new BufferedReader(inReader);
      String[] labels = readLabels(reader);
      if (labels == null) {
        in.close();
        return result;
      }
      while (reader.ready()) {
        FrozenParameters parameters = readParameter(parameterLabels, labels, reader);
        if (alreadyLoaded.contains(parameters))
          continue;
        result.add(parameters);
        alreadyLoaded.add(parameters);
      }
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      if (in != null)
        try {
          in.close();
        } catch (IOException e1) {
        }
    }
    return result;
  }

  private boolean canRead() {
    return new File(filepath).canRead();
  }

  public RunInfo infos() {
    return infos;
  }
}
