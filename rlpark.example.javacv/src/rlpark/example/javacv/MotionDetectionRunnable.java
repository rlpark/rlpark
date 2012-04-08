package rlpark.example.javacv;

import rlpark.plugin.opencv.FrameGrabber;
import rlpark.plugin.opencv.MotionMeasure;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
@SuppressWarnings("restriction")
public class MotionDetectionRunnable implements Runnable {
  private static final int DEPTH = opencv_core.IPL_DEPTH_32F;
  private final Clock clock = new Clock();
  private final FrameGrabber grabber = new FrameGrabber(0, DEPTH, 1);
  private final MotionMeasure motionMeasure;

  public MotionDetectionRunnable() {
    motionMeasure = new MotionMeasure(.99, grabber.width(), grabber.height(), DEPTH, 1);
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick()) {
      IplImage currentFrame = grabber.grab();
      if (currentFrame == null)
        return;
      motionMeasure.update(currentFrame);
    }
    grabber.dispose();
  }
}
