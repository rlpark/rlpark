package zephyr.plugin.critterview;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rlpark.plugin.critterbot.crtrlog.CrtrLogFile;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class FileHandler implements IFileHandler {

  @Override
  public List<String> extensions() {
    return Misc.asList("crtrlog.bz2", "crtrlog.zip", "crtrlog.gz", "crtrlog");
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  static public void handle(String filepath) {
    CrtrLogFile logfile = CrtrLogFile.load(filepath);
    Clock clock = new Clock(new File(filepath).getName());
    Zephyr.advertise(clock, logfile);
    while (clock.tick() && logfile.hasNextStep())
      logfile.step();
    logfile.close();
  }
}
