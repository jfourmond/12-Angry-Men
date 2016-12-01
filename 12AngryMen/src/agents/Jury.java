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
	
	protected double belief;
	protected boolean ready;
	protected boolean allowedToTalk;
	protected int nbVotes;
	
	protected AID[] juries;
	
	//	GETTERS
	public double getBelief() { return belief; }
	
	public boolean isAllowedToTalk() { return allowedToTalk; }
	
	//	SETTERS
	public void setBelief(double belief) { this.belief = belief; }
	
	public void setAllowedToTalk(boolean allowedToTalk) { this.allowedToTalk = allowedToTalk; }
	
	//	METHODES OBJECT
	public Belief belief() {
		if(belief < 0.8)
			return Belief.GUILTY;
		else
			return Belief.INNOCENT;
	}
	
	public abstract void influence(Argument argument);
	
	//	METHODES AGENT
	@Override
	protected void setup() {
		System.out.println(getLocalName() + ":: " + "Arrivée.");
		
		allowedToTalk = false;
		
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
		addBehaviour(new ReceiveAllowToTalk());
		addBehaviour(new ReceiveInfluence());
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
	
	public static boolean isJuryReceiver(ACLMessage message, AID jury) {
		Iterator<AID> it = message.getAllReceiver();
		while(it.hasNext()) {
			AID aid = it.next();
			if(aid.equals(jury))
				return true;
		}
		return false;
	}
	
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

		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage vote = new ACLMessage(ACLMessage.REQUEST);
			vote.addReceiver(juries[0]);
			vote.setContent(belief() + "");
			vote.setConversationId("change-vote");
			System.out.println(myAgent.getLocalName() + ":: REQUEST CHANGE VOTE " + belief());
			myAgent.send(vote);
		}
	}
	
	/**
	 * Comportement à exécution unique de demande au {@link Jury1} l'autorisation de parler
	 */
	protected class AskToTalk extends OneShotBehaviour {
		private static final long serialVersionUID = 4316551835965456089L;

		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage vote = new ACLMessage(ACLMessage.REQUEST);
			vote.addReceiver(juries[0]);
			vote.setConversationId("asking-to-talk");
			myAgent.send(vote);
		}
	}
	
	/**
	 * Comportement cyclique de réception de l'autorisation de parler
	 */
	protected class ReceiveAllowToTalk extends CyclicBehaviour {
		private static final long serialVersionUID = -2757249674321789741L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("allowing-to-talk");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
					allowedToTalk = true;
			} else
				block();
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
	
	protected class RejectArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -9098072165755661067L;
		
		private Argument argument;
		private ACLMessage message;
		private List<AID> juries;
		
		private double less = 0.3;
		
		public RejectArgument(ACLMessage message, Argument argument, AID ...juries) {
			this.argument = argument;
			this.juries = new ArrayList<>();
			for(AID jury : juries)
				this.juries.add(jury);
			this.message = message;
		}
		
		public RejectArgument(ACLMessage message, Argument argument, double less, AID ...juries) {
			this(message, argument, juries);
			this.less = less;
		}
		
		protected void addReceiver(ACLMessage message) {
			for(AID aid : juries)
				if(aid != myAgent.getAID())
					message.addReceiver(aid);
		}
		
		@Override
		public void action() {
			ACLMessage reject = message.createReply();
			reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
			argument.removeStrength(less);
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
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
						System.out.println(getLocalName() + ":: INFLUENCED BY " + argument);
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
	 * Comportement à exécution unique pour influencer un juré
	 */
	protected class Influence extends OneShotBehaviour {
		private static final long serialVersionUID = 3311163181612119608L;
		
		private ACLMessage message;
		private AID jury;
		private Argument argument;
		
		public Influence(ACLMessage message) throws UnreadableException {
			this.message = message;
			jury = message.getSender();
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
				System.out.println(myAgent.getLocalName() + ":: INFLUENCE " + jury.getLocalName());
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
}
