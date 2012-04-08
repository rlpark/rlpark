package rlpark.plugin.critterbot.actions;


public class XYThetaAction extends CritterbotAction {
  private static final long serialVersionUID = -1434106060178637255L;
  private static final int ActionValue = 30;
  public static final CritterbotAction NoMove = new XYThetaAction(0, 0, 0);
  public static final CritterbotAction TurnLeft = new XYThetaAction(ActionValue, ActionValue, ActionValue);
  public static final CritterbotAction TurnRight = new XYThetaAction(ActionValue, ActionValue, -ActionValue);
  public static final CritterbotAction Forward = new XYThetaAction(ActionValue, 0, 0);
  public static final CritterbotAction Backward = new XYThetaAction(-ActionValue, 0, 0);
  public static final CritterbotAction Left = new XYThetaAction(0, -ActionValue, 0);
  public static final CritterbotAction Right = new XYThetaAction(0, ActionValue, 0);

  public XYThetaAction(double x, double y, double theta) {
    super(MotorMode.XYTHETA_SPACE, x, y, theta);
  }

  public XYThetaAction(double[] actions) {
    super(MotorMode.XYTHETA_SPACE, actions);
  }

  static public CritterbotAction[] sevenActions() {
    return new CritterbotAction[] { NoMove, TurnLeft, TurnRight, Forward, Backward, Left, Right };
  }
}