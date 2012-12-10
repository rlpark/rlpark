package rlpark.plugin.rltoys.experiments.scheduling.internal.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClientInfo;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Message;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageBinary;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageClassData;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageJob;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageRequestClass;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageRequestJob;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageSendClientInfo;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;

public class SyncSocket {
  private final Socket socket;
  private final InputStream in;
  private final OutputStream out;

  public SyncSocket(Socket socket) {
    this.socket = socket;
    out = outputStream();
    in = inputStream();
  }

  private InputStream inputStream() {
    InputStream in = null;
    try {
      in = socket.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      close();
    }
    return in;
  }

  private OutputStream outputStream() {
    OutputStream out = null;
    try {
      out = socket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      close();
    }
    return out;
  }

  private MessageBinary transaction(Message message) {
    MessageBinary messageBinary = null;
    synchronized (out) {
      synchronized (in) {
        write(message);
        messageBinary = read();
      }
    }
    return messageBinary;
  }

  public MessageBinary read() {
    if (isClosed())
      return null;
    MessageBinary messageBinary = new MessageBinary();
    synchronized (in) {
      try {
        messageBinary.read(in);
      } catch (IOException e) {
        Messages.displayError(e);
        close();
        return null;
      }
    }
    Messages.debug(this.toString() + " reads " + messageBinary.type().toString());
    return messageBinary;
  }

  synchronized public void write(Message message) {
    Messages.debug(this.toString() + " writes " + message.type().toString());
    if (isClosed())
      return;
    synchronized (out) {
      try {
        message.write(out);
      } catch (IOException e) {
        Messages.displayError(e);
        close();
      }
    }
  }

  public MessageClassData classTransaction(String className) {
    Messages.println("Downloading code for " + className);
    MessageClassData messageClassData = null;
    try {
      MessageBinary message = transaction(new MessageRequestClass(className));
      messageClassData = (MessageClassData) Messages.cast(message, null);
    } catch (Throwable e) {
      Messages.displayError(e);
      close();
    }
    return messageClassData;
  }

  public MessageJob jobTransaction(ClassLoader classLoader) {
    MessageJob messageJobTodo = null;
    try {
      MessageBinary message = transaction(new MessageRequestJob());
      if (message == null)
        return null;
      messageJobTodo = (MessageJob) Messages.cast(message, classLoader);
    } catch (Throwable e) {
      Messages.displayError(e);
      close();
    }
    return messageJobTodo;
  }

  public void sendClientInfo(ClientInfo clientInfo) {
    write(new MessageSendClientInfo(clientInfo));
  }

  public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      Messages.displayError(e);
    }
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

  public static Message readNextMessage(SyncSocket clientSocket) {
    return readNextMessage(clientSocket, null);
  }

  public static Message readNextMessage(SyncSocket clientSocket, ClassLoader classLoader) {
    Message message = null;
    try {
      MessageBinary nextClientMessage = clientSocket.read();
      if (nextClientMessage == null)
        return null;
      message = Messages.cast(nextClientMessage, classLoader);
    } catch (Throwable e) {
      Messages.displayError(e);
      clientSocket.close();
      return null;
    }
    return message;
  }
}
