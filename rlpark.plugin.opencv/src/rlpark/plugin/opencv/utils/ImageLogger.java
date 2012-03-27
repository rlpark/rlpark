package rlpark.plugin.opencv.utils;

import java.io.File;
import java.io.IOException;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.TimedFileLogger;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Chrono;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_imgproc;

@SuppressWarnings("restriction")
public class ImageLogger {
  private final File folder;
  @Monitor
  private int imageCounter = 0;
  private final Chrono chrono = new Chrono();
  private final double period;
  private final TimedFileLogger logger;
  private final CvSize size;
  private IplImage resizedImage;

  public ImageLogger(String path, CvSize size, double period) {
    folder = new File(path);
    if (!folder.isDirectory())
      folder.mkdirs();
    System.out.println("Saving images in " + folder.getAbsolutePath());
    this.period = period;
    this.size = size;
    logger = createLogger();
  }

  private TimedFileLogger createLogger() {
    String loggerFilepath = folder.getAbsolutePath() + "/log.logtxt";
    try {
      final TimedFileLogger logger = new TimedFileLogger(loggerFilepath);
      logger.add("ImageCounter", new Monitored() {
        @SuppressWarnings("synthetic-access")
        @Override
        public double monitoredValue() {
          return imageCounter;
        }
      });
      return logger;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void saveImage(IplImage image) {
    if (chrono.getCurrentChrono() < period)
      return;
    if (resizedImage == null)
      resizedImage = opencv_core.cvCreateImage(size, image.depth(), image.nChannels());
    String filename = String.format("%s/image%09d.jpg", folder.getAbsolutePath(), imageCounter);
    opencv_imgproc.cvResize(image, resizedImage, opencv_imgproc.CV_INTER_LINEAR);
    opencv_highgui.cvSaveImage(filename, resizedImage);
    logger.update();
    chrono.start();
    imageCounter++;
  }

  public void dispose() {
    if (resizedImage != null)
      opencv_core.cvReleaseImage(resizedImage);
    logger.close();
  }
}
