/**
 *
 */

package dt;

import java.io.Serializable;
import java.util.*;


class Decisions implements Serializable{
  /**
	 * 
	 */
	private static final long serialVersionUID = -7567611094284315740L;
private Map<String, Attribute> decisions;

  public Decisions() {
    decisions = new HashMap<String, Attribute>();
  }

  public Map<String, Attribute> getMap() {
    return decisions;
  }

  public void put(String decision, Attribute attribute) {
    decisions.put(decision, attribute);
  }

  public void clear() {
    decisions.clear();
  }

  /**
   * Returns the attribute based on the decision matching the provided value.
   *
   * Throws BadDecisionException if no decision matches.
   */
  public Attribute apply(String value) throws BadDecisionException {
    Attribute result = decisions.get(value);

    if ( result == null )
      throw new BadDecisionException();

    return result;
  }
}
