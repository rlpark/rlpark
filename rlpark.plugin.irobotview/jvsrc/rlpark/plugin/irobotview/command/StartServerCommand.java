package rlpark.plugin.irobotview.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import rlpark.plugin.irobot.data.IRobotLabels;
import rlpark.plugin.irobot.internal.server.IRobotServer;
import rlpark.plugin.irobotview.dialog.SelectSerialPortDialog;

public abstract class StartServerCommand extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    SelectSerialPortDialog dialog = new SelectSerialPortDialog(Display.getCurrent().getActiveShell());
    dialog.open();
    Object[] results = dialog.getResult();
    String serialPortPath = results != null ? (String) results[0] : null;
    if (serialPortPath == null)
      return null;
    IRobotServer server = newServer(IRobotLabels.DiscoDefaultPort, serialPortPath);
    server.startDetach();
    if (server.isRunning())
      System.out.println("Server is listening on port " + server.port());
    return null;
  }

  abstract protected IRobotServer newServer(int port, String serialPortPath);
}
