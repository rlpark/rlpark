package rlpark.plugin.critterbot.actions;

import rlpark.plugin.rltoys.envio.actions.ActionArray;

public class CritterbotAction extends ActionArray {
  private static final long serialVersionUID = -1190160741892375081L;

  public enum MotorMode {
    WHEEL_SPACE, XYTHETA_SPACE, WHEEL_VOLTAGE
  };

  static public final CritterbotAction DoNothing = new WheelSpaceAction(null);

  public final MotorMode motorMode;

  public CritterbotAction(CritterbotAction critterbotAction) {
    this(critterbotAction.motorMode, critterbotAction.actions);
  }

  protected CritterbotAction(MotorMode motorMode, double... actions) {
    super(actions);
    assert actions == null || actions.length == 3;
    this.motorMode = motorMode;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj) && ((CritterbotAction) obj).motorMode == motorMode;
  }

  @Override
  public int hashCode() {
    return super.hashCode() + motorMode.ordinal();
  }
}
