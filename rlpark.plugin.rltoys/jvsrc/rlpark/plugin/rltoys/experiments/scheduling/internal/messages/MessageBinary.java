package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import static rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.Header;
import static rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.HeaderSize;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class MessageBinary extends Message {
  private int fullSizeRead;
  private byte[] content;

  public MessageBinary() {
    super(MessageType.Error);
  }

  private void throwAlignmentError() {
    throw new RuntimeException("Alignment error");
  }

  private void readHeader(InputStream in) throws IOException {
    DataInputStream dataIn = new DataInputStream(in);
    if (dataIn.readByte() != Header[0])
      throwAlignmentError();
    if (dataIn.readByte() != Header[1])
      throwAlignmentError();
    if (dataIn.readByte() != Header[2])
      throwAlignmentError();
    fullSizeRead = dataIn.readInt();
    type = Messages.MessageType.values()[dataIn.readInt()];
  }

  public void read(InputStream inputStream) throws IOException {
    readHeader(inputStream);
    content = new byte[fullSizeRead - HeaderSize];
    int dataToRead = content.length;
    while (dataToRead > 0) {
      int dataRead = inputStream.read(content, content.length - dataToRead, dataToRead);
      dataToRead -= dataRead;
    }
  }

  public byte[] content() {
    return content;
  }

  public InputStream contentInputStream() {
    return new ByteArrayInputStream(content);
  }
}
