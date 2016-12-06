package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import metiers.Argument;
import metiers.Belief;

/**
 * Classe abstraite {@link Jury} qui représente un Jury.
 */
public abstract class Jury extends Agent implements Serializable {
	private static final long serialVersionUID = 2075278590407410662L;

	protected static final int NB_JURIES = 12;
	
	protected Belief belief;
	protected boolean ready;
	protected int nbVotes;
	
	protected AID[] juries;
	
	//	GETTERS
	public Belief belief() { return belief; }
	
	//	SETTERS
	public void setBelief(Belief belief) { this.belief = belief; }
	
	//	METHODES OBJECT
	public void influence(Argument argument) { this.belief = argument.getBelief(); }
	
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
		
		belief = Belief.GUILTY;
		
		addBehaviour(new PerformReady());
		addBehaviour(new ReceivingVote());
		addBehaviour(new ReceiveInfluence());
		addBehaviour(new Leave());
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
	
	/**
	 * Retourne l'identifiant de l'Agent passé en paramètre
	 * @param agent : Agent
	 * @return l'identifiant de l'Agent passé en paramètre
	 */
	public static Integer getJuriesID(AID agent) {
		switch(agent.getLocalName()) {
			case "Jury1" :
				return 1;
			case "Jury2" : 
				return 2;
			case "Jury3" :
				return 3;
			case "Jury4" :
				return 4;
			case "Jury5" :
				return 5;
			case "Jury6" :
				return 6;
			case "Jury7" :
				return 7;
			case "Jury8" :
				return 8;
			case "Jury9" :
				return 9;
			case "Jury10" :
				return 10;
			case "Jury11" :
				return 11;
			case "Jury12" :
				return 12;
			default:
				return null;
		}
	}
	
	/**
	 * Test si l'Agent {@link Jury} passé en paramètre est destinataire du message passé en paramètre
	 * @param message : {@link ACLMessage} d'où les destinataires sont parcourus
	 * @param jury : destinataire à rechercher
	 * @return <code>true</code> si le Jury est destinataire, <code>false</code> sinon
	 */
	public static boolean isJuryReceiver(ACLMessage message, AID jury) {
		Iterator<AID> it = message.getAllReceiver();
		while(it.hasNext()) {
			AID aid = it.next();
			if(aid.equals(jury))
				return true;
		}
		return false;
	}
	
	/**
	 * Ajoute tous les jurés au message passé en paramètre
	 * @param message : {@link ACLMessage} dont les destinataires sont ajoutés
	 */
	public void addJuriesToMessage(ACLMessage message) {
		int id = getJuriesID(getAID());
		for(int i = 0; i < juries.length; ++i) {
			AID jury = juries[i];
			if(id != i+1 && !isJuryReceiver(message, jury))
				message.addReceiver(jury);
		}
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

	/**
	 * Comportement à exécution unique de demande de vote au {@link Jury1}
	 */
	protected class AskVote extends OneShotBehaviour {
		private static final long serialVersionUID = 8151209285061373832L;

		@Override
		public void action() {
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setConversationId("request-vote");
			request.addReceiver(juries[0]);
			System.out.println(myAgent.getLocalName() + ":: demande un vote au Jury 1.");
			myAgent.send(request);
		}	
	}
	
	/**
	 * Comportement cyclique de réception d'une demande de vote
	 */
	protected class ReceivingVote extends CyclicBehaviour {
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
	
	/**
	 * Comportement à exécution unique envoyant un vote au {@link Jury1}
	 */
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
			nbVotes++;
		}
	}
	
	protected class RequestChangeVote extends OneShotBehaviour {
		private static final long serialVersionUID = 597974196410566448L;

		public RequestChangeVote(Belief b) { belief = b; }
		
		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage vote = new ACLMessage(ACLMessage.REQUEST);
			vote.addReceiver(juries[0]);
			vote.setContent(belief + "");
			vote.setConversationId("change-vote");
			System.out.println(myAgent.getLocalName() + ":: REQUEST CHANGE VOTE " + belief());
			myAgent.send(vote);
		}
	}
	
	/**
	 * Comporement à exécution unique envoyant un argument aux jurés passé en paramètre au constructeur
	 */
	protected class ExposeArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 4384661119352078559L;

		protected Argument argument;
		protected List<AID> juries;
		
		public ExposeArgument(Argument argument, AID ...juries) {
			this.argument = argument;
			this.juries = new ArrayList<>();
			for(AID jury : juries)
				this.juries.add(jury);
		}
		
		protected void addReceiver(ACLMessage message) {
			for(AID aid : juries)
				if(!aid.equals(myAgent.getAID()))
					message.addReceiver(aid);
		}
		
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.PROPOSE);
			addReceiver(message);
			try {
				message.setContentObject(argument);
				message.setConversationId("argument");
				System.out.println(myAgent.getLocalName() + ":: PROPOSE " + argument);
				myAgent.send(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Comporement à exécution unique envoyant un argument revu aux jurés ayant refusé,
	 * ainsi qu'aux jurés passées en paramètre
	 */
	protected class ReviewArgument extends ExposeArgument {
		private static final long serialVersionUID = 6444800688132660995L;
		
		private Argument newArgument;
		private ACLMessage message;
		
		public ReviewArgument(ACLMessage message, Argument argument, AID ...juries) {
			super(argument, juries);
			this.message = message;
		}
		
		@Override
		protected void addReceiver(ACLMessage message) {
			for(AID aid : juries)
				if(!aid.equals(myAgent.getAID()) && !aid.equals(this.message.getSender()))
					message.addReceiver(aid);
		}
		
		@Override
		public void action() {
			ACLMessage propose = message.createReply();
			newArgument = new Argument(argument);
			try {
				addReceiver(propose);
				propose.setPerformative(ACLMessage.PROPOSE);
				propose.setContentObject(newArgument);
				System.out.println(myAgent.getLocalName() + ":: REVIEW PROPOSE : " + newArgument);
				myAgent.send(propose);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Comportement à exécution unique gérant le renvoi d'un argument.
	 */
	protected class RejectArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -9098072165755661067L;
		
		private Argument argument;
		private ACLMessage message;
		private List<AID> juries;
		
		public RejectArgument(ACLMessage message, Argument argument, AID ...juries) {
			this.argument = argument;
			this.juries = new ArrayList<>();
			for(AID jury : juries)
				this.juries.add(jury);
			this.message = message;
		}
		
		protected void addReceiver(ACLMessage message) {
			for(AID aid : juries)
				if(!aid.equals(myAgent.getAID()) && !aid.equals(this.message.getSender()))
					message.addReceiver(aid);
		}
		
		@Override
		public void action() {
			ACLMessage reject = message.createReply();
			reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
			try {
				addReceiver(reject);
				reject.setContentObject(argument);
				System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
				myAgent.send(reject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Comportement à exécution unique gérant l'acceptation d'un argument
	 */
	protected class AcceptArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 7487596991674698434L;
		
		private Argument argument;
		private ACLMessage message;
		
		public AcceptArgument(ACLMessage message, Argument argument) {
			this.argument = argument;
			this.message = message;
		}
		
		@Override
		public void action() {
			ACLMessage accept = message.createReply();
			accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			try {
				accept.setContentObject(argument);
				myAgent.send(accept);
				System.out.println(myAgent.getLocalName() + ":: ACCEPT " + argument);
				influence(argument);
				myAgent.addBehaviour(new InformFirstJury(argument.getBelief()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Comportement gérant l'influence d'un message sur l'Agent courant
	 */
	protected class ReceiveInfluence extends Behaviour {
		private static final long serialVersionUID = -4389290339335043917L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("influence");
			ACLMessage influence = myAgent.receive(mt);
			if(influence != null) {
				if(influence.getPerformative() == ACLMessage.PROPAGATE) {
					try {
						Argument argument = (Argument) influence.getContentObject();
						influence(argument);
						System.out.println(getLocalName() + ":: INFLUENCED BY " + argument + " NOW " + belief);
					} catch(UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}

		@Override
		public boolean done() { return belief() == Belief.INNOCENT; }
		
	}
	
	/**
	 * Comportement à exécution unique information le {@link Jury1} d'un changement d'opinion
	 */
	protected class InformFirstJury extends OneShotBehaviour {
		private static final long serialVersionUID = 1008776169018343746L;

		private Belief belief;
		
		public InformFirstJury(Belief belief) { this.belief = belief; }
		
		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
			try {
				inform.addReceiver(juries[0]);
				inform.setConversationId("inform-belief");
				inform.setContentObject(this.belief);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(inform);
		}
		
	}
	
	/**
	 * Comportement à exécution unique pour influencer un juré
	 */
	protected class Influence extends OneShotBehaviour {
		private static final long serialVersionUID = 3311163181612119608L;
		
		private ACLMessage message;
		private Argument argument;
		
		public Influence(ACLMessage message) throws UnreadableException {
			this.message = message;
			argument = (Argument) this.message.getContentObject();
		}

		@Override
		public void action() {
			ACLMessage message = this.message.createReply();
			try {
				message.setPerformative(ACLMessage.PROPAGATE);
				message.setContentObject(argument);
				message.setConversationId("influence");
				myAgent.send(message);
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
	
	/**
	 * Comportement captant une requête d'adieu
	 */
	protected class Leave extends CyclicBehaviour {
		private static final long serialVersionUID = 2140974085997359913L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("good-bye");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					myAgent.doDelete();
				}
			} else
				block();
		}
	}
}
