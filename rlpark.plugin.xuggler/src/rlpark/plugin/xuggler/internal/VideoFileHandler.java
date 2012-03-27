package rlpark.plugin.xuggler.internal;

import java.io.IOException;
import java.util.List;

import rlpark.plugin.xuggler.VideoPlayer;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class VideoFileHandler implements IFileHandler {
  public VideoFileHandler() {
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    VideoPlayer player = new VideoPlayer(filepath);
    Zephyr.advertise(player.clock(), player);
    player.run();
  }

  @Override
  public List<String> extensions() {
    return Misc.asList("mov", "mpg", "avi", "mp4", "m4v", "flv");
  }
}
