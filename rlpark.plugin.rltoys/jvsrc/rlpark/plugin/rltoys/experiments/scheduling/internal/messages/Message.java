package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class Message {
  protected MessageType type = MessageType.Error;

  protected Message(MessageType type) {
    this.type = type;
  }

  protected Message(Message message) {
    this.type = message.type();
  }

  public void write(OutputStream outputStream) throws IOException {
    ByteArrayOutputStream contentOut = new ByteArrayOutputStream();
    writeContentBuffer(contentOut);
    DataOutputStream socketOut = new DataOutputStream(new BufferedOutputStream(outputStream));
    socketOut.write(Messages.Header);
    socketOut.writeInt(Messages.HeaderSize + contentOut.size());
    socketOut.writeInt(type.ordinal());
    socketOut.write(contentOut.toByteArray());
    socketOut.flush();
  }

  @SuppressWarnings("unused")
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
  }

  public MessageType type() {
    return type;
  }
}
