/**
 * 
 */
package rlpark.plugin.critterbot.actions;

public class VoltageSpaceAction extends CritterbotAction {
  private static final long serialVersionUID = -5452282489229534809L;

  public VoltageSpaceAction(double motor0, double motor1, double motor2) {
    super(MotorMode.WHEEL_VOLTAGE, motor0, motor1, motor2);
  }

  public VoltageSpaceAction(double[] actions) {
    super(MotorMode.WHEEL_VOLTAGE, actions);
  }
}