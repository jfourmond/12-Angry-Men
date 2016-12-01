package metiers;

import jade.util.leap.Serializable;

public class Opinions implements Serializable {
	private static final long serialVersionUID = 804506260516157984L;
	
	private Belief opinions[];
	
	public Opinions(int size) {
		opinions = new Belief[12];
	}
	
	public Opinions(Belief opinions[]) {
		this.opinions = opinions;
	}
	
	//	GETTERS
	public Belief[] getOpinions() { return opinions; }
	
	//	METHODES
	public Belief getJuryOpinion(int idJury) { return opinions[idJury-1]; }
	
	public void setJuryOpinion(int idJury, Belief opinion) {
		opinions[idJury-1] = opinion;
	}
}
