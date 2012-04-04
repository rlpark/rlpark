package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class MessageJob extends Message {
  private Runnable[] jobs = new Runnable[] {};
  private int[] jobIds = new int[] {};

  public MessageJob(int jobId, Runnable done) {
    super(MessageType.Job);
    jobIds = new int[] { jobId };
    jobs = new Runnable[] { done };
  }

  public MessageJob(List<Runnable> jobs, List<Integer> jobIds) {
    super(MessageType.Job);
    assert jobIds.size() == jobs.size();
    this.jobIds = new int[jobIds.size()];
    for (int i = 0; i < jobIds.size(); i++)
      this.jobIds[i] = jobIds.get(i);
    this.jobs = new Runnable[jobs.size()];
    jobs.toArray(this.jobs);
  }

  protected MessageJob(MessageBinary message, ClassLoader classLoader) throws IOException {
    super(message);
    readContent(message.contentInputStream(), classLoader);
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    ObjectOutputStream objOut = new ObjectOutputStream(out);
    objOut.writeObject(jobIds);
    objOut.writeObject(jobs);
  }

  private void readContent(InputStream in, ClassLoader classLoader) throws IOException {
    ObjectInputStream objIn = ClassLoading.createObjectInputStream(in, classLoader);
    try {
      jobIds = (int[]) objIn.readObject();
      jobs = (Runnable[]) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public int[] jobIds() {
    return jobIds;
  }

  public Runnable[] jobs() {
    return jobs;
  }

  public int nbJobs() {
    return jobs.length;
  }
}
