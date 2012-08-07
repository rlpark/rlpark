package rlpark.plugin.rltoysview.internal.vectors;

import java.util.concurrent.Semaphore;

import org.eclipse.ui.IMemento;

import rlpark.plugin.rltoys.agents.functions.VectorProjection2D;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.utils.Eclipse;

@SuppressWarnings("restriction")
public class FunctionAdapter implements ContinuousFunction2D {
  static private final String MementoLabel = "projected";
  public Signal<ClassNode> projectedSet = new Signal<ClassNode>();
  private VectorProjection2D projection;
  private final Semaphore semaphore = new Semaphore(1);
  private RealVector projected;
  private ClassNode classNode;
  private String[] loadedPath;

  public FunctionAdapter() {
  }

  @Override
  public double value(double x, double y) {
    return projection.value(projected, x, y);
  }

  @Override
  public double minX() {
    return projection.minX();
  }

  @Override
  public double maxX() {
    return projection.maxX();
  }

  @Override
  public double minY() {
    return projection.minY();
  }

  @Override
  public double maxY() {
    return projection.maxY();
  }

  public RealVector lockProjected() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return projected;
  }

  public void unlockProjected() {
    semaphore.release();
  }

  public void setProjected(ClassNode classNode) {
    this.classNode = classNode;
    lockProjected();
    this.projected = (RealVector) classNode.instance();
    projectedSet.fire(classNode);
    unlockProjected();
  }

  public boolean canProject() {
    return projection != null && projected != null;
  }

  public void setProjection(VectorProjection2D projection) {
    this.projection = projection;
    findProjected();
  }

  private void findProjected() {
    if (projection == null || classNode != null || loadedPath == null)
      return;
    CodeNode loadedCodenode = ZephyrSync.syncCode().findNode(loadedPath);
    if (loadedCodenode == null)
      return;
    setProjected((ClassNode) loadedCodenode);
  }

  public void init(IMemento memento) {
    if (memento == null)
      return;
    IMemento child = memento.getChild(MementoLabel);
    if (child != null)
      loadedPath = Eclipse.loadPath(child);
  }

  public void saveState(IMemento memento) {
    String[] savedPath = classNode != null ? classNode.path() : loadedPath;
    if (savedPath == null)
      return;
    Eclipse.savePath(memento.createChild(MementoLabel), savedPath);
  }
}
