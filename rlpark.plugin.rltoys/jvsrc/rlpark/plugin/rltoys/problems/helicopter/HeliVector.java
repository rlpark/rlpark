package rlpark.plugin.rltoys.problems.helicopter;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class HeliVector {
  public double x;
  public double y;
  public double z;

  public HeliVector(HeliVector vecToCopy) {
    this.x = vecToCopy.x;
    this.y = vecToCopy.y;
    this.z = vecToCopy.z;
  }

  public HeliVector(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Quaternion to_quaternion() {
    Quaternion quat;
    double rotation_angle = Math.sqrt(x * x + y * y + z * z);
    if (rotation_angle < 1e-4) { // avoid division by zero -- also: can use
                                 // simpler computation in this case, since for
                                 // small angles sin(x) = x is a good
                                 // approximation
      quat = new Quaternion(x / 2.0f, y / 2.0f, z / 2.0f, 0.0f);
      quat.w = Math.sqrt(1.0f - (quat.x * quat.x + quat.y * quat.y + quat.z * quat.z));
    } else {
      quat = new Quaternion(Math.sin(rotation_angle / 2.0f) * (x / rotation_angle), Math.sin(rotation_angle / 2.0f)
          * (y / rotation_angle), Math.sin(rotation_angle / 2.0f) * (z / rotation_angle),
                            Math.cos(rotation_angle / 2.0f));
    }
    return quat;
  }

  public HeliVector rotate(Quaternion q) {
    return q.mult(new Quaternion(this)).mult(q.conj()).complex_part();
  }

  public HeliVector express_in_quat_frame(Quaternion q) {
    return this.rotate(q.conj());
  }

  public void reset() {
    x = 0.0;
    y = 0.0;
    z = 0.0;
  }
}