package rlpark.plugin.rltoysview.internal.vectors;


import rlpark.plugin.rltoys.agents.functions.VectorProjection2D;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoysview.internal.adapters.FunctionAdapter;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class VectorAdapter extends FunctionAdapter<RealVector> implements ContinuousFunction2D {
  private VectorProjection2D projection;

  public VectorAdapter() {
    super("projected");
  }

  @Override
  public boolean layoutFunctionIsSet() {
    return projection != null && super.layoutFunctionIsSet();
  }

  @Override
  protected void findLayoutFunctionNode() {
    if (projection == null)
      return;
    super.findLayoutFunctionNode();
  }

  @Override
  public double value(double x, double y) {
    return projection.value(layoutFunction(), x, y);
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

  public void setProjection(VectorProjection2D projection) {
    this.projection = projection;
    findLayoutFunctionNode();
  }
}
