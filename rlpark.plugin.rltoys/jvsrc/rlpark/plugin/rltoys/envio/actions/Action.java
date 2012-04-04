package rlpark.plugin.rltoys.envio.actions;

import java.io.Serializable;

public interface Action extends Serializable {
  static public final Action ActionUndef = new Action() {
    private static final long serialVersionUID = -3715038451723221733L;
  };
}
