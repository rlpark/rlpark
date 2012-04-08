package rlpark.plugin.critterbot.internal;

import java.io.IOException;

import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropArray;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.disco.io.DiscoSocket;
import rlpark.plugin.robot.internal.sync.Syncs;
import rlpark.plugin.robot.observations.ObservationReceiver;
import rlpark.plugin.robot.observations.ObservationVersatile;

public class CritterbotSound implements ObservationReceiver {
  private final String hostname;
  private final int port;
  private DiscoSocket socket;
  public static final int NbMicrophoneBin = 9;
  private final Drop soundFFTDrop = new Drop("FftAudioDrop", new DropData[] {
      new DropArray(CritterbotLabels.MicrophoneFFT + "Left", NbMicrophoneBin),
      new DropArray(CritterbotLabels.MicrophoneFFT + "Right", NbMicrophoneBin) });
  private final DropScalarGroup soundData = new DropScalarGroup(soundFFTDrop);

  public CritterbotSound(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  @Override
  public void initialize() {
    try {
      socket = new DiscoSocket(hostname, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ObservationVersatile waitForData() {
    DiscoPacket packet = null;
    try {
      packet = socket.recv();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return packet != null ? Syncs.createObservation(System.currentTimeMillis(), packet.byteBuffer(), soundData) : null;
  }

  @Override
  public boolean isClosed() {
    return socket == null || socket.isSocketClosed();
  }

  public Legend legend() {
    return soundData.legend();
  }

  @Override
  public int packetSize() {
    return soundFFTDrop.dataSize();
  }
}
