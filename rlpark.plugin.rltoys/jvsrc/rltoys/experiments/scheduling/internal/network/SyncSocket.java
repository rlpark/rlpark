package rltoys.experiments.scheduling.internal.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import rltoys.experiments.scheduling.internal.messages.Message;
import rltoys.experiments.scheduling.internal.messages.MessageBinary;
import rltoys.experiments.scheduling.internal.messages.MessageClassData;
import rltoys.experiments.scheduling.internal.messages.MessageJob;
import rltoys.experiments.scheduling.internal.messages.MessageRequestClass;
import rltoys.experiments.scheduling.internal.messages.MessageRequestJob;
import rltoys.experiments.scheduling.internal.messages.MessageSendClientName;
import rltoys.experiments.scheduling.internal.messages.Messages;

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
    MessageBinary messageBinary;
    synchronized (out) {
      synchronized (in) {
        write(message);
        messageBinary = read();
      }
    }
    return messageBinary;
  }

  public MessageBinary read() {
    MessageBinary messageBinary = new MessageBinary();
    if (isClosed())
      return messageBinary;
    synchronized (in) {
      try {
        messageBinary.read(in);
      } catch (IOException e) {
        Messages.displayError(e);
        close();
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
    MessageBinary message = transaction(new MessageRequestClass(className));
    MessageClassData messageClassData = (MessageClassData) Messages.cast(message, null);
    return messageClassData;
  }

  public MessageJob jobTransaction(ClassLoader classLoader) {
    MessageBinary message = transaction(new MessageRequestJob());
    MessageJob messageJobTodo = (MessageJob) Messages.cast(message, classLoader);
    return messageJobTodo;
  }

  public void sendClientName() {
    String localhostName = "unknown";
    try {
      localhostName = java.net.InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
    }
    write(new MessageSendClientName(localhostName));
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
    return Messages.cast(clientSocket.read(), classLoader);
  }
}
