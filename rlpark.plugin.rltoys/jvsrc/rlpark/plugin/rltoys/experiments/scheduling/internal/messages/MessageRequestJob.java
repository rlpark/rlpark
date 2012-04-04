package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class MessageRequestJob extends Message {
  public MessageRequestJob() {
    super(MessageType.RequestJob);
  }

  protected MessageRequestJob(MessageBinary message) {
    super(message);
  }
}
