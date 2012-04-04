package rlpark.plugin.rltoys.experiments.scheduling.pools;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.PoolResult;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;


public class FileJobPool extends AbstractJobPool {
  class FileJobIterator implements Iterator<Runnable> {
    final ObjectInputStream objin;
    private Runnable nextJob;

    FileJobIterator(File file) {
      try {
        objin = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))));
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      nextJob = readJob();
    }

    private Runnable readJob() {
      try {
        return (Runnable) objin.readObject();
      } catch (EOFException e) {
        close(objin);
        return null;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    public boolean hasNext() {
      return nextJob != null;
    }

    @Override
    public Runnable next() {
      Runnable job = nextJob;
      nextJob = readJob();
      return job;
    }

    @Override
    public void remove() {
    }
  }

  static private int nbFilePool = 0;
  private final ObjectOutputStream objout;
  private final File file;
  private final String name;
  private final Chrono chrono = new Chrono();
  private int nbJobs = 0;

  public FileJobPool(JobPoolListener onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    this("pool" + nbFilePool, onAllJobDone, onJobDone);
  }

  public FileJobPool(String name, JobPoolListener onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    super(onAllJobDone, onJobDone);
    this.name = name;
    nbFilePool++;
    try {
      file = File.createTempFile("jobpool", null);
      objout = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void onPoolStart() {
    Messages.println(String.format("Starting %s: %d jobs to do", name, nbJobs));
    chrono.start();
  }

  @Override
  protected void onPoolEnd() {
    Messages.println(String.format("Closing %s: %d jobs in %s", name, nbJobs, chrono.toString()));
  }


  @Override
  public void add(Runnable job) {
    checkHasBeenSubmitted();
    nbJobs++;
    try {
      objout.writeObject(job);
      objout.reset();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PoolResult submitTo(Scheduler scheduler) {
    close(objout);
    if (nbJobs > 0)
      return super.submitTo(scheduler);
    poolResult = new PoolResult();
    poolResult.poolDone();
    return poolResult;
  }

  void close(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Iterator<Runnable> createIterator() {
    return new FileJobIterator(file);
  }
}
