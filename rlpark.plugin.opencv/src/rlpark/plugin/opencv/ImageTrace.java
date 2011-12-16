package rlpark.plugin.opencv;

import java.awt.image.BufferedImage;

import rlpark.plugin.opencv.zephyr.OpenCVImageProvider;
import zephyr.plugin.core.api.viewable.ImageProvider;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageTrace implements ImageProvider {
  public final double lambda;
  private double d = 0.0;
  private final ImageBuffer buffer01;
  private final ImageBuffer average;
  private final ImageBuffer trace;
  private final OpenCVImageProvider provider = new OpenCVImageProvider();

  public ImageTrace(double lambda, int width, int height, int depth, int channels) {
    this.lambda = lambda;
    trace = new ImageBuffer(width, height, depth, channels);
    buffer01 = new ImageBuffer(width, height, depth, channels);
    average = new ImageBuffer(width, height, depth, channels);
  }

  public IplImage update(IplImage currentFrame) {
    opencv_core.cvScale(average.im(), average.im(), lambda, 0.0);
    opencv_core.cvScale(currentFrame, buffer01.im(), 1.0 - lambda, 0.0);
    opencv_core.cvAdd(average.im(), buffer01.im(), average.im(), null);
    d = lambda * d + (1 - lambda);
    opencv_core.cvScale(average.im(), trace.im(), 1.0 / d, 0.0);
    provider.update(trace.im());
    return trace.im();
  }

  @Override
  public BufferedImage image() {
    return provider.image();
  }

  public IplImage im() {
    return trace.im();
  }

  public void dispose() {
    average.dispose();
    buffer01.dispose();
    trace.dispose();
    provider.dispose();
  }
}
