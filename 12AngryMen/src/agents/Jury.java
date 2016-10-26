package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import metiers.Guilt;

/**
 * 
 * 
 */
public abstract class Jury extends Agent {
	private static final long serialVersionUID = 2075278590407410662L;

	protected double belief;
	
	//	GETTERS
	public double getBelief() { return belief; }
	
	//	SETTERS
	public void setBelief(double belief) { this.belief = belief; }
	
	//	METHODES OBJECT
	public Guilt belief() {
		if(belief < 0.5)
			return Guilt.GUILTY;
		else
			return Guilt.INNOCENT;
	}
	
	//	METHODES AGENT
	@Override
	protected void setup() {
		System.out.println(getLocalName() + ":: " + "Arrivée.");
		
		DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
			sd.setType("jury");
			sd.setName("JADE-12-angry-men");
			dfd.addServices(sd);
		try {
			//	Enregistrement dans les "pages jaunes"
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	@Override
	protected void takeDown() {
		// Suppression des pages jaunes
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println(getLocalName() + ":: " + "Départ.");
	}
}
