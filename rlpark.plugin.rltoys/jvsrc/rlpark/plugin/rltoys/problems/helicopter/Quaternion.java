package rlpark.plugin.rltoys.problems.helicopter;

public class Quaternion {
  public double x;
  public double y;
  public double z;
  public double w;

  public Quaternion(Quaternion qToCopy) {
    this.x = qToCopy.x;
    this.y = qToCopy.y;
    this.z = qToCopy.z;
    this.w = qToCopy.w;
  }

  public Quaternion(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Quaternion(HeliVector v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
    this.w = 0.0;
  }

  public Quaternion conj() {
    return new Quaternion(-x, -y, -z, w);
  }

  public HeliVector complex_part() {
    return new HeliVector(this.x, this.y, this.z);
  }

  public Quaternion mult(Quaternion rq) {
    return new Quaternion(this.w * rq.x + this.x * rq.w + this.y * rq.z - this.z * rq.y, this.w * rq.y - this.x * rq.z
        + this.y * rq.w + this.z * rq.x, this.w * rq.z + this.x * rq.y - this.y * rq.x + this.z * rq.w, this.w * rq.w
        - this.x * rq.x - this.y * rq.y - this.z * rq.z);
  }

  void stringSerialize(StringBuffer b) {
    b.append("x_" + x);
    b.append("y_" + y);
    b.append("z_" + z);
    b.append("w_" + w);
  }

  public void reset() {
    x = 0.0;
    y = 0.0;
    z = 0.0;
    w = 1.0;
  }
}
