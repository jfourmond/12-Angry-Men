package metiers;

import java.io.Serializable;

public class Argument implements Serializable {
	private static final long serialVersionUID = -208958373724499420L;
	
	private static int ID = 0;
	
	private int id;
	private double strength;
	
	//	CONSTRUCTEURS
	public Argument() {
		this.id = ++ID;
		this.strength = 0.5;
	}
	
	public Argument(Argument argument) {
		this.id = ++ID;
		this.strength = argument.strength;
		giveStrength(0.1);
	}
	
	//	GETTERS
	public int getId() { return id; }
	
	public double getStrength() { return strength; }
	
	//	SETTERS
	public void setId(int id) { this.id = id; }
	
	public void setStrength(double strength) { this.strength = strength; }
	
	// METHODES
	public void giveStrength(double add) {
		strength += add;
		if(strength > 1.0) strength = 1.0;
	}
	
	public void removeStrength(double less) {
		strength -= less;
		if(strength < 0) strength = 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Argument : { id: " + id + ", strength: " + strength + " }");
		return sb.toString();
	}
}
