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
	
	//	GETTERS
	public int getId() { return id; }
	
	public double getStrength() { return strength; }
	
	//	SETTERS
	public void setId(int id) { this.id = id; }
	
	public void setStrength(double strength) { this.strength = strength; }
	
	// METHODES
	public void giveStrength() { strength++; }
	
	public void removeStrength() { strength--; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Argument : { id: " + id + ", strength: " + strength + " }");
		return sb.toString();
	}
}
