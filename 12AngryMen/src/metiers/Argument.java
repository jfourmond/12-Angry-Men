package metiers;

import java.io.Serializable;

public class Argument implements Serializable {
	private static final long serialVersionUID = -208958373724499420L;
	
	private static int ID = 0;
	
	private int id;
	private double strength;
	
	//	CONSTRUCTEURS
	public Argument(double strength) {
		this.id = ++ID;
		this.strength = strength;
	}
	
	public Argument(int id, double strength) {
		this.id = id;
		this.strength = strength;
	}
	
	//	GETTERS
	public int getId() { return id; }
	
	public double getStrength() { return strength; }
	
	//	SETTERS
	public void setId(int id) { this.id = id; }
	
	public void setStrength(double strength) { this.strength = strength; }
	
	// METHODES
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Argument : { id: " + id + ", strength: " + strength + " }");
		return sb.toString();
	}
}
