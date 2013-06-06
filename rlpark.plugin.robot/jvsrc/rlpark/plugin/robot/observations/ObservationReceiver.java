package rlpark.plugin.robot.observations;


public interface ObservationReceiver {
  void initialize();

  int packetSize();

  ObservationVersatile waitForData();

  boolean isClosed();

  void close();
}
