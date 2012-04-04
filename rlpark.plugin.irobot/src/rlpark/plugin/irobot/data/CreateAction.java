package rlpark.plugin.irobot.data;

import rlpark.plugin.rltoys.envio.actions.ActionArray;

public class CreateAction extends ActionArray {
  private static final long serialVersionUID = -3988828504637894677L;
  public static final int Left = 0;
  public static final int Right = 1;
  public static final int DefaultVelocity = 200;
  public static final CreateAction DontMove = new CreateAction(0, 0);
  public static final CreateAction SpinLeftForward = new CreateAction(DefaultVelocity, 0);
  public static final CreateAction SpinLeftBackward = new CreateAction(-DefaultVelocity, 0);
  public static final CreateAction SpinRightForward = new CreateAction(0, DefaultVelocity);
  public static final CreateAction SpinRightBackward = new CreateAction(0, -DefaultVelocity);
  public static final CreateAction SpinLeft = new CreateAction(-DefaultVelocity, DefaultVelocity);
  public static final CreateAction SpinRight = new CreateAction(DefaultVelocity, -DefaultVelocity);
  public static final CreateAction Backward = new CreateAction(-DefaultVelocity, -DefaultVelocity);
  public static final CreateAction Forward = new CreateAction(DefaultVelocity, DefaultVelocity);

  public static final CreateAction[] AllActions = { DontMove, SpinLeftForward, SpinLeftBackward, SpinRightForward,
      SpinRightBackward, SpinLeft, SpinRight, Backward, Forward };

  public static final CreateAction DoNothing = new CreateAction(null);

  public CreateAction(double wheelLeft, double wheelRight) {
    super(wheelLeft, wheelRight);
  }

  public CreateAction(double[] actions) {
    super(actions);
  }

  public double right() {
    return actions[Right];
  }

  public double left() {
    return actions[Left];
  }

  public void set(double wheelLeft, double wheelRight) {
    actions[Left] = wheelLeft;
    actions[Right] = wheelRight;
  }
}
