package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;


public class MessageSendClientName extends Message {
  private final String clientName;

  public MessageSendClientName(String clientName) {
    super(MessageType.SendClientName);
    this.clientName = clientName;
  }

  protected MessageSendClientName(MessageBinary message) throws IOException {
    super(message);
    clientName = readClientname(message.contentInputStream());
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeUTF(clientName);
  }

  private String readClientname(InputStream in) throws IOException {
    return new DataInputStream(in).readUTF();
  }

  public String clientName() {
    return clientName;
  }
}
