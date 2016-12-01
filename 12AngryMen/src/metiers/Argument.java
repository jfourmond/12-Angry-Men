package metiers;

import java.io.Serializable;

public class Argument implements Serializable {
	private static final long serialVersionUID = -208958373724499420L;
	
	private static int ID = 0;
	
	private int id;
	private double strength;
	private Belief belief;
	
	//	CONSTRUCTEURS
	public Argument(Belief belief) {
		this.id = ++ID;
		this.strength = 0.5;
		this.belief = belief;
	}
	
	public Argument(Argument argument) {
		this.id = ++ID;
		this.strength = argument.strength;
		this.belief = argument.belief;
		giveStrength(0.1);
	}
	
	//	GETTERS
	public int getId() { return id; }
	
	public double getStrength() { return strength; }
	
	public Belief getBelief() { return belief; }
	
	//	SETTERS
	public void setId(int id) { this.id = id; }
	
	public void setStrength(double strength) { this.strength = strength; }
	
	public void setBelief(Belief belief) { this.belief = belief; }
	
	// METHODES
	/**
	 * Augmenter de <code>add</code> la force
	 * @param add : valeur à incrémenter à la force
	 */
	public void giveStrength(double add) {
		strength += add;
		if(strength > 1.0) strength = 1.0;
	}
	
	/**
	 * Diminuer de <code>less</code> la force
	 * @param less : valeur à décrémenter à la force
	 */
	public void removeStrength(double less) {
		strength -= less;
		if(strength < 0) strength = 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Argument : { id: " + id + ", " + belief + ", strength: " + strength + " }");
		return sb.toString();
	}
}
