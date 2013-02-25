package rlpark.plugin.rltoysview.internal.adapters;


import java.util.concurrent.Semaphore;

import org.eclipse.ui.IMemento;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.helpers.CodeNodeToInstance;
import zephyr.plugin.core.utils.Eclipse;

@SuppressWarnings("restriction")
abstract public class FunctionAdapter<T> {
  public Signal<ClassNode> layoutFunctionSet = new Signal<ClassNode>();
  private T layoutFunction;
  private final Semaphore semaphore = new Semaphore(1);
  private ClassNode classNode;
  private CodeNodeToInstance<T> toInstance = new CodeNodeToInstance.Default<T>();
  private final String mementoLabel;
  private String[] loadedPath;

  public FunctionAdapter(String mementoLabel) {
    this.mementoLabel = mementoLabel;
  }

  protected T layoutFunction() {
    assert semaphore.availablePermits() == 0;
    return layoutFunction;
  }

  public T lockLayoutFunction() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return layoutFunction;
  }

  public void setCodeNodeToInstance(CodeNodeToInstance<T> toInstance) {
    this.toInstance = toInstance;
  }

  public void unlockLayoutFunction() {
    semaphore.release();
  }

  public void setLayoutFunction(ClassNode classNode) {
    this.classNode = classNode;
    lockLayoutFunction();
    this.layoutFunction = toInstance.toInstance(classNode);
    layoutFunctionSet.fire(classNode);
    unlockLayoutFunction();
  }

  public boolean layoutFunctionIsSet() {
    return layoutFunction != null;
  }

  protected void findLayoutFunctionNode() {
    if (classNode != null || loadedPath == null)
      return;
    CodeNode loadedCodenode = ZephyrSync.syncCode().findNode(loadedPath);
    if (loadedCodenode == null)
      return;
    setLayoutFunction((ClassNode) loadedCodenode);
  }

  public void init(IMemento memento) {
    if (memento == null)
      return;
    IMemento child = memento.getChild(mementoLabel);
    if (child != null)
      loadedPath = Eclipse.loadPath(child);
  }

  public void saveState(IMemento memento) {
    String[] savedPath = classNode != null ? classNode.path() : loadedPath;
    if (savedPath == null)
      return;
    Eclipse.savePath(memento.createChild(mementoLabel), savedPath);
  }
}