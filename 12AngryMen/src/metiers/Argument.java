package metiers;

import java.io.Serializable;

public class Argument implements Serializable {
	private static final long serialVersionUID = -208958373724499420L;
	
	private double strength;
	
	//	CONSTRUCTEURS
	public Argument(double strength) {
		this.strength = strength;
	}
	
	//	GETTERS
	public double getStrength() { return strength; }
	
	//	SETTERS
	public void setStrength(double strength) { this.strength = strength; }
}
