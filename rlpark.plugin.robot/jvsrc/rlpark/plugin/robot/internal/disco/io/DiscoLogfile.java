package rlpark.plugin.robot.internal.disco.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;


public class DiscoLogfile implements Iterator<DiscoPacket> {
  private ObjectInputStream objin;
  private DiscoPacket nextPacket;
  public final String filepath;

  public DiscoLogfile(String name) throws IOException {
    this.filepath = name;
    InputStream input = new FileInputStream(name);
    if (name.endsWith(".gz"))
      input = new GZIPInputStream(input);
    objin = new ObjectInputStream(new BufferedInputStream(input));
    nextPacket = readPacket();
  }

  private DiscoPacket readPacket() {
    try {
      return (DiscoPacket) objin.readObject();
    } catch (EOFException e) {
      return null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean hasNext() {
    return nextPacket != null;
  }

  public void close() {
    try {
      objin.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    objin = null;
  }

  @Override
  public void remove() {
  }

  @Override
  public DiscoPacket next() {
    DiscoPacket result = nextPacket;
    nextPacket = readPacket();
    return result;
  }

  public boolean isClosed() {
    return objin == null;
  }
}
