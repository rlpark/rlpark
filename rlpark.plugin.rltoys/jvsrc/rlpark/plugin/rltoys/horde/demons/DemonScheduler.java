package rlpark.plugin.rltoys.horde.demons;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.MovingAverage;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Scheduling;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Chrono;

public class DemonScheduler implements Serializable {
  private static final long serialVersionUID = 6003588160245867945L;

  protected class DemonUpdater implements Runnable {
    private final int offset;

    DemonUpdater(int offset) {
      this.offset = offset;
    }

    @Override
    public void run() {
      int currentPosition = offset;
      while (currentPosition < demons.size()) {
        if (throwable != null)
          return;
        try {
          demons.get(currentPosition).update(x_t, a_t, x_tp1);
        } catch (Throwable throwable) {
          DemonScheduler.this.throwable = throwable;
          return;
        }
        currentPosition += nbThread;
      }
    }
  }

  transient private ExecutorService executor = null;
  transient private DemonUpdater[] updaters;
  transient private Future<?>[] futurs;
  transient private Chrono chrono;
  transient Throwable throwable = null;
  protected List<? extends Demon> demons;
  protected RealVector x_tp1;
  protected RealVector x_t;
  protected Action a_t;
  protected final int nbThread;
  @Monitor
  private final MovingAverage updateTimeAverage = new MovingAverage(100);

  public DemonScheduler() {
    this(Scheduling.getDefaultNbThreads());
  }

  public DemonScheduler(int nbThread) {
    this.nbThread = nbThread;
  }

  private void initialize() {
    updaters = new DemonUpdater[nbThread];
    for (int i = 0; i < updaters.length; i++)
      updaters[i] = new DemonUpdater(i);
    futurs = new Future<?>[nbThread];
    executor = Scheduling.newFixedThreadPool("demons", nbThread);
    chrono = new Chrono();
  }

  public void update(List<? extends Demon> demons, RealVector x_t, Action a_t, RealVector x_tp1) {
    if (executor == null)
      initialize();
    throwable = null;
    chrono.start();
    this.x_t = x_t;
    this.a_t = a_t;
    this.x_tp1 = x_tp1;
    this.demons = demons;
    for (int i = 0; i < updaters.length; i++)
      futurs[i] = executor.submit(updaters[i]);
    try {
      for (Future<?> futur : futurs)
        futur.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (throwable != null)
      throw new RuntimeException(throwable);
    updateTimeAverage.update(chrono.getCurrentMillis());
  }

  public double updateTimeAverage() {
    return updateTimeAverage.average();
  }
}
