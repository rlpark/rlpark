package rlpark.plugin.opencv.zephyr;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import java.awt.image.BufferedImage;

import rlpark.plugin.opencv.ImageBuffer;
import rlpark.plugin.opencv.OpenCVUtils;
import zephyr.plugin.core.api.viewable.ImageProvider;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;

public class OpenCVImageProvider implements ImageProvider {
  private IplImage frame;
  private IplImage depthBuffer;
  private IplImage channelBuffer;

  @Override
  public BufferedImage image() {
    if (frame == null)
      return null;
    IplImage depthConverted = convertDepthIFN(frame);
    IplImage channelConverted = convertChannelIFN(depthConverted);
    return channelConverted.getBufferedImage();
  }

  private IplImage convertChannelIFN(IplImage src) {
    if (src.nChannels() == 3)
      return src;
    if (channelBuffer == null)
      channelBuffer = IplImage.create(src.width(), src.height(), src.depth(), 3);
    opencv_imgproc.cvCvtColor(src, channelBuffer, opencv_imgproc.CV_GRAY2RGB);
    return channelBuffer;
  }

  private IplImage convertDepthIFN(IplImage src) {
    if (src.depth() == opencv_core.IPL_DEPTH_8U)
      return src;
    if (depthBuffer == null)
      depthBuffer = IplImage.create(src.width(), src.height(), IPL_DEPTH_8U, src.nChannels());
    OpenCVUtils.convertScale(src, depthBuffer);
    return depthBuffer;
  }

  public void update(IplImage currentFrame) {
    this.frame = currentFrame;
  }

  public void dispose() {
    if (depthBuffer != null)
      depthBuffer.release();
    if (channelBuffer != null)
      depthBuffer.release();
  }

  public void update(ImageBuffer currentFrame) {
    update(currentFrame.im());
  }
}
