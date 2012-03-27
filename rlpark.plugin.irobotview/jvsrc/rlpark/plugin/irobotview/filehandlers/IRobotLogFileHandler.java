package rlpark.plugin.irobotview.filehandlers;

import static rlpark.plugin.irobot.logfiles.IRobotLogFile.Extension;

import java.io.IOException;
import java.util.List;

import rlpark.plugin.irobot.logfiles.IRobotLogFile;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class IRobotLogFileHandler implements IFileHandler {

  @Override
  public List<String> extensions() {
    return Misc.asList(Extension + ".bz2", Extension + ".zip", Extension + ".gz", Extension);
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  static public void handle(String filepath) {
    IRobotLogFile logfile = new IRobotLogFile(filepath);
    Clock clock = new Clock(filepath);
    Zephyr.advertise(clock, logfile);
    while (clock.tick() && logfile.hasNextStep())
      logfile.step();
    logfile.close();
  }
}
