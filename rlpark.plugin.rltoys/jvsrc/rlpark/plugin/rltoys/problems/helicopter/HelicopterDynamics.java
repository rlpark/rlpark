/* Helicopter Domain for RL - Competition - RLAI's Port of Pieter Abbeel's code submission
 * Copyright (C) 2007, Pieter Abbeel
 * Ported by Mark Lee and Brian Tanner from C++ to Java for the 2008 RL-Competition and beyond. 
 * Adapted to RLPark from http://library.rl-community.org/wiki/Helicopter_%28Java%29
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 */
package rlpark.plugin.rltoys.problems.helicopter;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class HelicopterDynamics {

  /* some constants indexing into the helicopter's state */
  static final int ndot_idx = 0; // north velocity
  static final int edot_idx = 1; // east velocity
  static final int ddot_idx = 2; // down velocity
  static final int n_idx = 3; // north
  static final int e_idx = 4; // east
  static final int d_idx = 5; // down
  static final int p_idx = 6; // angular rate around forward axis
  static final int q_idx = 7; // angular rate around sideways (to the right)
                              // axis
  static final int r_idx = 8; // angular rate around vertical (downward) axis
  static final int qx_idx = 9; // quaternion entries, x,y,z,w q = [ sin(theta/2)
                               // * axis; cos(theta/2)]
  static final int qy_idx = 10; // where axis = axis of rotation; theta is
                                // amount of rotation around that axis
  static final int qz_idx = 11; // [recall: any rotation can be represented by a
                                // single rotation around some axis]
  final static int qw_idx = 12;
  final static int state_size = 13;

  static final int NUMOBS = 12;
  // note: observation returned is not the state itself, but the "error state"
  // expressed in the helicopter's frame (which allows for a simpler mapping
  // from observation to inputs)
  // observation consists of:
  // u, v, w : velocities in helicopter frame
  // xerr, yerr, zerr: position error expressed in frame attached to helicopter
  // [xyz correspond to ned when helicopter is in "neutral" orientation, i.e.,
  // level and facing north]
  // p, q, r
  // qx, qy, qz
  private final double wind[] = new double[2];

  // upper bounds on values state variables can take on (required by rl_glue to
  // be put into a string at environment initialization)
  public static final double MaxVel = 5.0; // m/s
  public static final double MaxPos = 20.0;
  public static final double MaxRate = 2 * 3.1415 * 2;
  public static final double MAX_QUAT = 1.0;
  public static final double MIN_QW_BEFORE_HITTING_TERMINAL_STATE = Math.cos(30.0 / 2.0 * Math.PI / 180.0);
  public static final double MaxAction = 1.0;
  public static final double WIND_MAX = 5.0; //
  public static final Range[] ObservationRanges = new Range[] { new Range(-MaxVel, MaxVel), new Range(-MaxVel, MaxVel),
      new Range(-MaxVel, MaxVel), new Range(-MaxPos, MaxPos), new Range(-MaxPos, MaxPos), new Range(-MaxPos, MaxPos),
      new Range(-MaxRate, MaxRate), new Range(-MaxRate, MaxRate), new Range(-MaxRate, MaxRate),
      new Range(-MAX_QUAT, MAX_QUAT), new Range(-MAX_QUAT, MAX_QUAT), new Range(-MAX_QUAT, MAX_QUAT) };
  public static final Range[] ActionRanges = new Range[] { new Range(-MaxAction, MaxAction),
      new Range(-MaxAction, MaxAction), new Range(-MaxAction, MaxAction), new Range(-MaxAction, MaxAction) };
  // very crude helicopter model, okay around hover:
  public static final double heli_model_u_drag = 0.18;
  public static final double heli_model_v_drag = 0.43;
  public static final double heli_model_w_drag = 0.49;
  public static final double heli_model_p_drag = 12.78;
  public static final double heli_model_q_drag = 10.12;
  public static final double heli_model_r_drag = 8.16;
  public static final double heli_model_u0_p = 33.04;
  public static final double heli_model_u1_q = -33.32;
  public static final double heli_model_u2_r = 70.54;
  public static final double heli_model_u3_w = -42.15;
  public static final double heli_model_tail_rotor_side_thrust = -0.54;
  public static final double DeltaT = .1; // simulation time scale [time scale
                                          // for
  // control ---
  // internally we integrate at 100Hz for simulating the
  // dynamics]
  @Monitor
  final public HeliVector velocity = new HeliVector(0.0d, 0.0d, 0.0d);
  @Monitor
  final public HeliVector position = new HeliVector(0.0d, 0.0d, 0.0d);
  @Monitor
  final public HeliVector angularRate = new HeliVector(0.0d, 0.0d, 0.0d);
  public Quaternion q = new Quaternion(0.0d, 0.0d, 0.0d, 1.0d);
  final private double noise[] = new double[6];
  final private Random random;

  public HelicopterDynamics(Random random) {
    this.random = random;
  }

  public void reset() {
    velocity.reset();
    position.reset();
    angularRate.reset();
    q.reset();
  }

  private double boxMull() {
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    return Math.sqrt(-2.0f * Math.log(x1)) * Math.cos(2.0f * Math.PI * x2);
  }

  public double[] getObservation() {
    double[] observation = new double[NUMOBS];
    // observation is the error state in the helicopter's coordinate system
    // (that way errors/observations can be mapped more directly to actions)
    HeliVector ned_error_in_heli_frame = position.express_in_quat_frame(q);
    HeliVector uvw = velocity.express_in_quat_frame(q);

    observation[0] = uvw.x;
    observation[1] = uvw.y;
    observation[2] = uvw.z;

    observation[n_idx] = ned_error_in_heli_frame.x;
    observation[e_idx] = ned_error_in_heli_frame.y;
    observation[d_idx] = ned_error_in_heli_frame.z;
    observation[p_idx] = angularRate.x;
    observation[q_idx] = angularRate.y;
    observation[r_idx] = angularRate.z;

    // the error quaternion gets negated, b/c we consider the rotation required
    // to bring the helicopter back to target in the helicopter's frame
    observation[qx_idx] = q.x;
    observation[qy_idx] = q.y;
    observation[qz_idx] = q.z;

    for (int i = 0; i < NUMOBS; i++)
      observation[i] = ObservationRanges[i].bound(observation[i]);
    return observation;
  }

  public void step(ActionArray agentAction) {
    double[] a = new double[4];
    // saturate all the actions, b/c the actuators are limited:
    // [real helicopter's saturation is of course somewhat different, depends on
    // swash plate mixing etc ... ]
    for (int a_i = 0; a_i < a.length; a_i++)
      a[a_i] = ActionRanges[a_i].bound(agentAction.actions[a_i]);


    final double noise_mult = 2.0;
    final double noise_std[] = { 0.1941, 0.2975, 0.6058, 0.1508, 0.2492, 0.0734 }; // u,
                                                                                   // v,
                                                                                   // w,
                                                                                   // p,
                                                                                   // q,
                                                                                   // r
    double noise_memory = .8;
    // generate Gaussian random numbers

    for (int i = 0; i < 6; ++i)
      noise[i] = noise_memory * noise[i] + (1.0d - noise_memory) * boxMull() * noise_std[i] * noise_mult;

    for (int t = 0; t < 10; ++t) {

      // Euler integration:

      // *** position ***
      position.x += DeltaT * velocity.x;
      position.y += DeltaT * velocity.y;
      position.z += DeltaT * velocity.z;

      // *** velocity ***
      HeliVector uvw = velocity.express_in_quat_frame(q);
      HeliVector wind_ned = new HeliVector(wind[0], wind[1], 0.0);
      HeliVector wind_uvw = wind_ned.express_in_quat_frame(q);
      HeliVector uvw_force_from_heli_over_m = new HeliVector(-heli_model_u_drag * (uvw.x + wind_uvw.x) + noise[0],
                                                             -heli_model_v_drag * (uvw.y + wind_uvw.y)
                                                                 + heli_model_tail_rotor_side_thrust + noise[1],
                                                             -heli_model_w_drag * uvw.z + heli_model_u3_w * a[3]
                                                                 + noise[2]);

      HeliVector ned_force_from_heli_over_m = uvw_force_from_heli_over_m.rotate(q);
      velocity.x += DeltaT * ned_force_from_heli_over_m.x;
      velocity.y += DeltaT * ned_force_from_heli_over_m.y;
      velocity.z += DeltaT * (ned_force_from_heli_over_m.z + 9.81d);

      // *** orientation ***
      HeliVector axis_rotation = new HeliVector(angularRate.x * DeltaT, angularRate.y * DeltaT, angularRate.z * DeltaT);
      Quaternion rot_quat = axis_rotation.to_quaternion();
      q = q.mult(rot_quat);

      // *** angular rate ***
      double p_dot = -heli_model_p_drag * angularRate.x + heli_model_u0_p * a[0] + noise[3];
      double q_dot = -heli_model_q_drag * angularRate.y + heli_model_u1_q * a[1] + noise[4];
      double r_dot = -heli_model_r_drag * angularRate.z + heli_model_u2_r * a[2] + noise[5];

      angularRate.x += DeltaT * p_dot;
      angularRate.y += DeltaT * q_dot;
      angularRate.z += DeltaT * r_dot;
    }
  }

  public boolean isCrashed() {
    return Math.abs(position.x) > MaxPos || Math.abs(position.y) > MaxPos || Math.abs(position.z) > MaxPos;
  }
}
