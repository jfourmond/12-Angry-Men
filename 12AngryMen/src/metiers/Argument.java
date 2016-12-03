package metiers;

import java.io.Serializable;

public class Argument implements Serializable {
	private static final long serialVersionUID = -208958373724499420L;
	
	private static int ID = 0;
	
	private int id;
	private Belief belief;
	
	//	CONSTRUCTEURS
	public Argument(Belief belief) {
		this.id = ++ID;
		this.belief = belief;
	}
	
	public Argument(Argument argument) {
		this.id = ++ID;
		this.belief = argument.belief;
	}
	
	//	GETTERS
	public int getId() { return id; }
	
	public Belief getBelief() { return belief; }
	
	//	SETTERS
	public void setId(int id) { this.id = id; }
	
	public void setBelief(Belief belief) { this.belief = belief; }
	
	// METHODES
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Argument : { id: " + id + ", " + belief + " }");
		return sb.toString();
	}
}
