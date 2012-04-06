package rlpark.plugin.irobot.internal.serial;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;


public class SerialPorts {
  static Map<String, CommPortIdentifier> portIdentifiers = new LinkedHashMap<String, CommPortIdentifier>();

  @SuppressWarnings("unchecked")
  static public void refreshPortIdentifiers() {
    portIdentifiers.clear();
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    while (portList.hasMoreElements()) {
      CommPortIdentifier portId = portList.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
        portIdentifiers.put(portId.getName(), portId);
    }
  }

  public static CommPortIdentifier getPortIdentifier(String filename) {
    return portIdentifiers.get(filename);
  }

  static public Collection<String> getSerialPortsList() {
    refreshPortIdentifiers();
    return new ArrayList<String>(portIdentifiers.keySet());
  }
}
