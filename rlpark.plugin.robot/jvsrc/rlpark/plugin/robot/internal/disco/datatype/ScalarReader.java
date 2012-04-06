package rlpark.plugin.robot.internal.disco.datatype;

import rlpark.plugin.robot.internal.sync.LiteByteBuffer;


public interface ScalarReader extends Ranged {
  int getInt(LiteByteBuffer buffer);

  double getDouble(LiteByteBuffer buffer);
}
