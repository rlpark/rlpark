package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class MessageClassData extends Message {
  private final String className;
  private final byte[] classData;

  public MessageClassData(String className) {
    super(MessageType.ClassData);
    this.className = className;
    classData = null;
  }

  protected MessageClassData(MessageBinary message) {
    super(message);
    className = null;
    classData = message.content();
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    String classAsPath = className.replace('.', '/') + ".class";
    try {
      Class<?> classRef = Class.forName(className);
      ClassLoader localClassLoader = classRef.getClassLoader();
      if (localClassLoader == null)
        return;
      InputStream stream = localClassLoader.getResourceAsStream(classAsPath);
      out.write(IOUtils.toByteArray(stream));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public byte[] classData() {
    return classData;
  }
}
