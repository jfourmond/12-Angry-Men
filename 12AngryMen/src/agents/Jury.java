package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Serializable;
import metiers.Guilt;

/**
 * Classe abstraite {@link Jury} qui représente un Jury.
 */
public abstract class Jury extends Agent implements Serializable {
	private static final long serialVersionUID = 2075278590407410662L;

	protected static final int NB_JURIES = 12;
	
	protected double belief;
	protected boolean ready;
	
	protected AID[] juries;
	
	//	GETTERS
	public double getBelief() { return belief; }
	
	//	SETTERS
	public void setBelief(double belief) { this.belief = belief; }
	
	//	METHODES OBJECT
	public Guilt belief() {
		if(belief < 0.2)
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
		
		addBehaviour(new PerformReady());
		addBehaviour(new ReceivingVote());
	}
	
	@Override
	protected void takeDown() {
		// Suppression des pages jaunes
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println(getLocalName() + ":: Départ.");
	}
	
	//	CLASSES INTERNES COMPORTEMENTS
	/**
	 * Comportement de réception d'une demande de préparation
	 */
	private class PerformReady extends Behaviour {
		private static final long serialVersionUID = -4978924344665073082L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("juries-ready");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					try {
						juries = (AID[]) reply.getContentObject();
						if(juries == null)
							block();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}

		@Override
		public boolean done() {
			return (juries != null && juries.length == 12);
		}
		
		@Override
		public int onEnd() {
			ready = true;
			System.out.println(getLocalName() + ":: Prêt.");
			return super.onEnd();
		}
	}

	private class ReceivingVote extends CyclicBehaviour {
		private static final long serialVersionUID = -5188161158793172186L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("asking-vote");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					addBehaviour(new PerformVote());
				}
			} else
				block();
		}
	}
	
	protected class PerformVote extends OneShotBehaviour {
		private static final long serialVersionUID = -2925922941582026625L;

		@Override
		public void action() {
			// Envoi du vote au Jury 1
			ACLMessage vote = new ACLMessage(ACLMessage.INFORM);
			vote.addReceiver(juries[0]);
			
			vote.setContent(belief() + "");
			vote.setConversationId("juries-vote");
			myAgent.send(vote);
		}
	}
	
}
