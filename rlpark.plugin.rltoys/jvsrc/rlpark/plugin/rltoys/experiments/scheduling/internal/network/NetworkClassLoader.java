package rlpark.plugin.rltoys.experiments.scheduling.internal.network;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClassLoading.NetworkFindClass;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageClassData;


public class NetworkClassLoader extends ClassLoader implements NetworkFindClass {
  private final SyncSocket socket;
  private final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();
  private boolean isDisposed = false;
  private ClassLoader contextClassLoader = null;

  public NetworkClassLoader(SyncSocket socket) {
    this.socket = socket;
  }

  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if (isDisposed)
      return null;
    return super.loadClass(name, resolve);
  }

  @Override
  synchronized public Class<?> findClass(String name) {
    if (isDisposed)
      return null;
    Class<?> result = null;
    if (contextClassLoader != null)
      try {
        result = contextClassLoader.loadClass(name);
      } catch (ClassNotFoundException e1) {
      }
    if (result != null)
      return result;
    result = cache.get(name);
    if (result != null)
      return result;
    try {
      MessageClassData messageClassData = socket.classTransaction(name);
      if (messageClassData == null)
        return null;
      byte[] classData = messageClassData.classData();
      result = defineClass(name, classData, 0, classData.length, getClass().getProtectionDomain());
      cache.put(name, result);
      return result;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  public void dispose() {
    isDisposed = true;
  }

  static public NetworkClassLoader newClassLoader(final SyncSocket socket) {
    return AccessController.doPrivileged(new PrivilegedAction<NetworkClassLoader>() {
      @Override
      public NetworkClassLoader run() {
        return new NetworkClassLoader(socket);
      }
    });
  }

  public void setDefaultClassLoader(ClassLoader contextClassLoader) {
    this.contextClassLoader = contextClassLoader;
  }
}
