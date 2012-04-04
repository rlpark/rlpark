package rlpark.plugin.rltoys.experiments.parametersweep.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.internal.monitoring.helpers.Loggers;

@SuppressWarnings("restriction")
public class ParametersLogFileWriter {
  public final String filepath;
  private RunInfo referenceInfos;

  public ParametersLogFileWriter(String filepath) {
    this.filepath = filepath;
  }

  private boolean canRead() {
    return new File(filepath).canRead();
  }

  public void writeParameters(List<FrozenParameters> unsortedResultingParameters) {
    try {
      List<FrozenParameters> resultingParameters = new ArrayList<FrozenParameters>(unsortedResultingParameters);
      Collections.sort(resultingParameters);
      LoggerRow loggerRow = new LoggerRow(filepath);
      loggerRow.writeLegend(resultingParameters.get(0).labels());
      for (FrozenParameters parameters : resultingParameters) {
        checkInfo(parameters);
        loggerRow.writeRow(parameters.values());
      }
      loggerRow.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void appendParameters(Parameters mutableParameters) {
    FrozenParameters parameters = mutableParameters.froze();
    boolean canRead = canRead();
    try {
      Loggers.checkParentFolder(this.filepath);
      checkInfo(parameters);
      LoggerRow loggerRow = new LoggerRow(new PrintWriter(new FileOutputStream(filepath, true), true));
      if (!canRead)
        loggerRow.writeLegend(parameters.labels());
      loggerRow.writeRow(parameters.values());
      loggerRow.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkInfo(FrozenParameters parameters) throws IOException {
    RunInfo infos = parameters.infos();
    if (referenceInfos == null) {
      referenceInfos = infos;
      writeInfoReferenceInfos();
      return;
    }
    if (!referenceInfos.equals(infos))
      throw new RuntimeException("Infos do not match previous parameters");
  }

  private void writeInfoReferenceInfos() throws IOException {
    String infoFile = ParametersLogFileReader.toInfoFilepath(filepath);
    LoggerRow loggerRow = new LoggerRow(infoFile);
    loggerRow.writeLegend(referenceInfos.infoLabels());
    loggerRow.writeRow(referenceInfos.infoValues());
    loggerRow.close();
  }

  public void reorganizeLogFile(String... parameterLabelsArray) {
    ParametersLogFileReader reader = new ParametersLogFileReader(filepath);
    List<FrozenParameters> logFileData = reader.extractParameters(parameterLabelsArray);
    writeParameters(logFileData);
  }
}
