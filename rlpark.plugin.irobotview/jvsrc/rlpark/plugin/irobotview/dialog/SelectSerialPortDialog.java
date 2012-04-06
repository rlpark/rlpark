package rlpark.plugin.irobotview.dialog;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import rlpark.plugin.irobot.internal.serial.SerialPorts;

public class SelectSerialPortDialog extends SelectionDialog {
  static final private String CuPrefix = "/dev/cu.";

  public SelectSerialPortDialog(Shell parent) {
    super(parent);
    setTitle("Select a Serial Port");
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite dialogArea = (Composite) super.createDialogArea(parent);
    Composite content = new Composite(dialogArea, SWT.NONE);
    content.setLayout(new GridLayout(1, false));
    GridData gd = new GridData(GridData.FILL_BOTH);
    content.setLayoutData(gd);
    final List list = new List(content, SWT.BORDER | SWT.V_SCROLL);
    list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    fillList(list);
    list.addListener(SWT.Selection, new Listener() {
      @SuppressWarnings("synthetic-access")
      @Override
      public void handleEvent(Event e) {
        setSelectionResult(new String[] { list.getItem(list.getSelectionIndex()) });
      }
    });
    return dialogArea;
  }

  @Override
  protected void cancelPressed() {
    setSelectionResult(null);
    super.cancelPressed();
  }

  private void fillList(List list) {
    Collection<String> ports = SerialPorts.getSerialPortsList();
    boolean isCuPortAvailable = checkForCuPort(ports);
    for (String port : ports)
      if (!isCuPortAvailable || port.startsWith(CuPrefix))
        list.add(port);
  }

  private boolean checkForCuPort(Collection<String> ports) {
    for (String port : ports)
      if (port.startsWith(CuPrefix))
        return true;
    return false;
  }
}
