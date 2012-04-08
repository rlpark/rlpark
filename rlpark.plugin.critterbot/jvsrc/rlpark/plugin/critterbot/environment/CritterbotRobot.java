package rlpark.plugin.critterbot.environment;

import rlpark.plugin.critterbot.internal.CritterbotConnection;
import rlpark.plugin.critterbot.internal.CritterbotSound;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.internal.sync.ObservationSynchronizer;
import rlpark.plugin.robot.observations.ObservationReceiver;
import rlpark.plugin.robot.observations.ObservationVersatile;

public class CritterbotRobot extends CritterbotEnvironment {
  public enum SoundMode {
    None, FFT
  }

  static public final SoundMode defaultSoundMode = SoundMode.None;

  static final Integer DiscoControlPort = 2330;
  static final String WalterIP = "10.0.1.10";
  static final String GremlinIP = "10.0.1.20";
  private final Legend legend;
  private final double[] lastSoundData;
  private final ObservationSynchronizer soundSync;

  public CritterbotRobot() {
    this(true, defaultSoundMode);
  }

  public CritterbotRobot(boolean onCritterbotNetwork) {
    this(onCritterbotNetwork, defaultSoundMode);
  }

  public CritterbotRobot(SoundMode soundMode) {
    this(true, soundMode);
  }

  public CritterbotRobot(boolean onCritterbotNetwork, SoundMode soundMode) {
    this(new CritterbotConnection(onCritterbotNetwork ? WalterIP : "localhost", DiscoControlPort), soundMode);
  }

  public CritterbotRobot(ObservationReceiver receiver, SoundMode soundMode) {
    super(receiver);
    CritterbotSound soundConnection = soundMode == SoundMode.None ? null : new CritterbotSound(WalterIP, 7001);
    legend = soundConnection == null ? super.legend() : buildLegend(soundConnection);
    lastSoundData = soundConnection == null ? null : new double[soundConnection.legend().nbLabels()];
    soundSync = soundConnection == null ? null : new ObservationSynchronizer(soundConnection, false);
  }

  private Legend buildLegend(CritterbotSound soundConnection) {
    Legend robotLegend = super.legend();
    Legend soundLegend = soundConnection.legend();
    String[] labels = new String[robotLegend.nbLabels() + soundLegend.nbLabels()];
    int index = 0;
    for (String label : robotLegend.getLabels()) {
      labels[index] = label;
      index++;
    }
    for (String label : soundLegend.getLabels()) {
      labels[index] = label;
      index++;
    }
    return new Legend(labels);
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public double[] waitNewObs() {
    double[] robotObs = super.waitNewObs();
    if (robotObs == null)
      return null;
    if (lastSoundData == null)
      return robotObs;
    ObservationVersatile soundUpdatedObs = soundSync.newObsNow().last();
    if (soundUpdatedObs != null)
      System.arraycopy(soundUpdatedObs.doubleValues(), 0, lastSoundData, 0, lastSoundData.length);
    double[] obs = new double[robotObs.length + lastSoundData.length];
    System.arraycopy(robotObs, 0, obs, 0, robotObs.length);
    System.arraycopy(lastSoundData, 0, obs, robotObs.length, lastSoundData.length);
    return obs;
  }
}
