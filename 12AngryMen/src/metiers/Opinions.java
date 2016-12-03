package metiers;

import jade.util.leap.Serializable;

public class Opinions implements Serializable {
	private static final long serialVersionUID = 804506260516157984L;
	
	private Belief opinions[];
	private int sent;
	
	public Opinions(int size) {
		opinions = new Belief[12];
		sent = 0;
	}
	
	//	GETTERS
	public Belief[] getOpinions() { return opinions; }
	
	public int sent() { return sent; }
	
	//	METHODES
	public Belief getJuryOpinion(int idJury) { return opinions[idJury-1]; }
	
	public void setJuryOpinion(int idJury, Belief opinion) { opinions[idJury-1] = opinion; }
	
	public void incrementSent() { sent++; }
	
	public int innocentCount() {
		int count = 0;
		for(Belief belief : opinions)
			if(belief == Belief.INNOCENT)
				count ++;
		return count;
	}
	
	public int guiltyCount() {
		int count = 0;
		for(Belief belief : opinions)
			if(belief == Belief.GUILTY)
				count ++;
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder ch = new StringBuilder("[ ");
		for(int i=0; i<opinions.length; i++) {
			ch.append((i+1) + " : " + opinions[i]);
			if(i != opinions.length -1)
				ch.append(", ");
		}
		ch.append("]\n");
		ch.append("INNOCENT : " + innocentCount() + " | GUILTY : " + guiltyCount());
		return ch.toString();
	}
}
