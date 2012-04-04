/**
 * 
 */
package critterbot;

import rlpark.plugin.rltoys.agents.Agent;
import critterbot.actions.CritterbotAction;

/**
 * Create your own interface with your own main loop
 */
@Deprecated
public interface CritterbotAgent extends Agent {
  @Override
  CritterbotAction getAtp1(double[] obs);
}