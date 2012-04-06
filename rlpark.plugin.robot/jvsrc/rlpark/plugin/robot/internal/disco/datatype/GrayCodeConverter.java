package rlpark.plugin.robot.internal.disco.datatype;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;


public interface GrayCodeConverter {
  void convert(LiteByteBuffer source, LiteByteBuffer target);
}
