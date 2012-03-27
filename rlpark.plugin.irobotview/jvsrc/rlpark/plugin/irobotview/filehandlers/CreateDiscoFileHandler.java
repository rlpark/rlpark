package rlpark.plugin.irobotview.filehandlers;

import static rlpark.plugin.irobot.logfiles.CreateBinaryLogfile.Extension;

import java.io.IOException;
import java.util.List;

import rlpark.plugin.irobot.logfiles.CreateBinaryLogfile;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class CreateDiscoFileHandler implements IFileHandler {

  @Override
  public List<String> extensions() {
    return Misc.asList(Extension + ".gz", Extension);
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  static public void handle(String filepath) {
    CreateBinaryLogfile logfile;
    try {
      logfile = new CreateBinaryLogfile(filepath);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Clock clock = new Clock(filepath);
    Zephyr.advertise(clock, logfile);
    while (clock.tick() && logfile.hasNextStep())
      logfile.nextStep();
    logfile.close();
  }
}
