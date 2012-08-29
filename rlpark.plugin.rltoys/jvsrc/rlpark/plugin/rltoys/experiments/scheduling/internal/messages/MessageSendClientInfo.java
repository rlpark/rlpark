package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;


public class MessageSendClientInfo extends Message {
  private final ClientInfo clientInfo;

  public MessageSendClientInfo(ClientInfo clientInfo) {
    super(MessageType.SendClientName);
    this.clientInfo = clientInfo;
  }

  protected MessageSendClientInfo(MessageBinary message) throws IOException {
    super(message);
    ObjectInputStream objIn = ClassLoading.createObjectInputStream(message.contentInputStream(), null);
    try {
      clientInfo = (ClientInfo) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    new ObjectOutputStream(out).writeObject(clientInfo);
  }

  public ClientInfo clientInfo() {
    return clientInfo;
  }
}
