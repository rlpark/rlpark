package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoading {
  public interface NetworkFindClass {
    Class<?> findClass(String name);
  }

  private static boolean forceNetworkClassResolution = false;

  public static ObjectInputStream createObjectInputStream(InputStream in, final ClassLoader classLoader)
      throws IOException {
    if (classLoader == null)
      return new ObjectInputStream(in);
    return new ObjectInputStream(in) {
      @Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();
        if (forceFindClass(className))
          return ((NetworkFindClass) classLoader).findClass(className);
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException exc) {
          try {
            return classLoader.loadClass(className);
          } catch (ClassNotFoundException e) {
          }
        }
        throw new ClassNotFoundException(className);
      }
    };
  }

  public static void enableForceNetworkClassResolution() {
    forceNetworkClassResolution = true;
  }

  public static boolean forceFindClass(String name) {
    if (!forceNetworkClassResolution)
      return false;
    if (name.startsWith("java."))
      return false;
    return name.contains("$");
  }
}
