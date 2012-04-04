package rlpark.plugin.rltoys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {

  class StreamReader extends Thread {
    private final BufferedReader reader;

    public StreamReader(InputStream input) {
      InputStreamReader inputReader = new InputStreamReader(input);
      reader = new BufferedReader(inputReader);
    }

    @Override
    public void run() {
      try {
        String line = "";
        while (process != null) {
          line = reader.readLine();
          if (line != null)
            System.out.println(label + ": " + line);
          else
            break;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  protected Process process;
  protected final String label;
  private StreamReader outputThread;
  private StreamReader errorThread;
  private final String[] commandLine;

  public Command(String label, String... commandLine) {
    this.label = label;
    this.commandLine = commandLine;
  }

  public void start() throws IOException {
    process = Runtime.getRuntime().exec(commandLine);
    outputThread = new StreamReader(process.getInputStream());
    errorThread = new StreamReader(process.getErrorStream());
    outputThread.start();
    errorThread.start();
  }

  public void waitFor() throws InterruptedException {
    process.waitFor();
    process = null;
  }

  public void kill() {
    if (process != null) {
      process.destroy();
      process = null;
    }
    synchronized (outputThread) {
      outputThread.notifyAll();
    }
    synchronized (errorThread) {
      errorThread.notifyAll();
    }
  }
}
