package rlpark.plugin.rltoys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Utils {
  final public static double EPSILON = 10e-8;

  public static <T> Set<T> asSet(T... ts) {
    return new LinkedHashSet<T>(asList(ts));
  }

  public static <T> List<T> asList(T... ts) {
    return Arrays.asList(ts);
  }

  public static <T> LinkedList<T> asLinkedList(T... ts) {
    return new LinkedList<T>(asList(ts));
  }

  public static <T, U> Map<T, U> asMap(T key, U value) {
    Map<T, U> result = new LinkedHashMap<T, U>();
    result.put(key, value);
    return result;
  }

  public static boolean checkValue(double value) {
    return !Double.isInfinite(value) && !Double.isNaN(value);
  }

  public static boolean checkProbability(double value) {
    return value >= 0 && value <= 1;
  }

  public static String[] asStringArray(Collection<String> collection) {
    String[] result = new String[collection.size()];
    collection.toArray(result);
    return result;
  }

  public static int[] asIntArray(Collection<Integer> collection) {
    int[] result = new int[collection.size()];
    int index = 0;
    for (Integer i : collection) {
      result[index] = i;
      index++;
    }
    return result;
  }

  public static double[] asDoubleArray(List<Double> list) {
    double[] result = new double[list.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = list.get(i);
    return result;
  }

  public static <T> T choose(Random random, List<T> list) {
    if (random == null)
      return list.get(0);
    return list.get(random.nextInt(list.size()));
  }

  public static <T> T choose(Random random, T... elements) {
    return elements[random.nextInt(elements.length)];
  }

  public static <T> T choose(Random random, Collection<T> set) {
    return choose(random, new ArrayList<T>(set));
  }

  public static double trunc(double value, double threshold) {
    return Math.max(Math.min(value, threshold), -threshold);
  }

  public static <T> T first(Iterable<T> iterable) {
    return iterable.iterator().next();
  }

  public static Object[] asObjectArray(Collection<?> collection) {
    Object[] result = new Object[collection.size()];
    int i = 0;
    for (Object o : collection) {
      result[i] = o;
      i++;
    }
    return result;
  }

  public static int[] range(int imin, int imax) {
    int[] result = new int[imax - imin];
    for (int i = 0; i < result.length; i++)
      result[i] = imin + i;
    return result;
  }

  public static File createTempFile(String prefix) {
    try {
      return File.createTempFile(prefix, "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean checkInstanciated(Object[] array) {
    for (Object o : array)
      if (o == null)
        return false;
    return true;
  }

  static public void notSupported() {
    throw new RuntimeException("Operation not supported");
  }

  static public void notImplemented() {
    throw new RuntimeException("Operation not implemented");
  }

  static final public double square(double a) {
    return a * a;
  }

  static public double discountToTimeSteps(double discount) {
    assert discount >= 0 && discount < 1.0;
    return 1 / (1 - discount);
  }

  static public double timeStepsToDiscount(int timeSteps) {
    assert timeSteps > 0;
    return 1.0 - 1.0 / timeSteps;
  }

  public static String[] concat(String[] array01, String... array02) {
    String[] result = new String[array01.length + array02.length];
    System.arraycopy(array01, 0, result, 0, array01.length);
    System.arraycopy(array02, 0, result, array01.length, array02.length);
    return result;
  }

  public static double[] concat(double[] array01, double... array02) {
    double[] result = new double[array01.length + array02.length];
    System.arraycopy(array01, 0, result, 0, array01.length);
    System.arraycopy(array02, 0, result, array01.length, array02.length);
    return result;
  }

  public static <T> T newInstance(Class<T> type, Object... args) {
    Class<?>[] classArgs = new Class<?>[args.length];
    for (int i = 0; i < classArgs.length; i++)
      classArgs[i] = args[i].getClass();
    return newInstance(type, classArgs, args);
  }

  public static <T> T newInstance(Class<T> type, Class<?>[] classArgs, Object... args) {
    Constructor<T> constructor = null;
    try {
      constructor = type.getConstructor(classArgs);
    } catch (SecurityException e) {
      e.printStackTrace();
      return null;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
    }
    try {
      return constructor.newInstance(args);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void save(Serializable serialized, String filepath) {
    save(serialized, new File(filepath));
  }

  public static void save(Serializable serialized, File file) {
    try {
      OutputStream fout = new FileOutputStream(file);
      if (file.getName().endsWith(".gz"))
        fout = new GZIPOutputStream(fout);
      ObjectOutputStream out = new ObjectOutputStream(fout);
      out.writeObject(serialized);
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object load(String filepath) {
    return load(new File(filepath));
  }

  //
  // public static Object load(File file) {
  // try {
  // FileInputStream fis = new FileInputStream(file);
  // ObjectInputStream in = new ObjectInputStream(fis);
  // Object serialized = in.readObject();
  // in.close();
  // return serialized;
  // } catch (IOException e) {
  // throw new RuntimeException(e);
  // } catch (ClassNotFoundException e) {
  // throw new RuntimeException(e);
  // }
  // }

  public static Object load(File file) {
    return load(file, new ClassLoader[] { Thread.currentThread().getContextClassLoader() });
  }

  public static Object load(File file, final ClassLoader... loaders) {
    try {
      InputStream fis = new FileInputStream(file);
      if (file.getName().endsWith(".gz"))
        fis = new GZIPInputStream(fis);
      ObjectInputStream oIn = new ObjectInputStream(fis) {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
          String className = desc.getName();
          try {
            return Class.forName(className);
          } catch (ClassNotFoundException exc) {
            for (ClassLoader cl : loaders) {
              try {
                return cl.loadClass(className);
              } catch (ClassNotFoundException e) {
              }
            }
            throw new ClassNotFoundException(className);
          }
        }
      };
      Object serialized = oIn.readObject();
      oIn.close();
      return serialized;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static public double[] newFilledArray(int length, double value) {
    double[] result = new double[length];
    Arrays.fill(result, value);
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> T clone(T t) {
    Method method;
    try {
      method = t.getClass().getMethod("clone");
    } catch (SecurityException e) {
      e.printStackTrace();
      return null;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
    }
    try {
      return (T) method.invoke(t);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
