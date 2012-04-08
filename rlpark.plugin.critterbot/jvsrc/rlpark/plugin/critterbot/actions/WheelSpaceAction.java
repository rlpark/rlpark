/**
 * 
 */
package rlpark.plugin.critterbot.actions;

public class WheelSpaceAction extends CritterbotAction {
  private static final long serialVersionUID = 8800756883418406775L;

  public WheelSpaceAction(double motor0, double motor1, double motor2) {
    super(MotorMode.WHEEL_SPACE, motor0, motor1, motor2);
  }

  public WheelSpaceAction(double[] actions) {
    super(MotorMode.WHEEL_SPACE, actions);
  }
}