package agents;

import java.io.IOException;
import java.util.LinkedList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends Jury {
	private static final long serialVersionUID = 4874292225851563156L;
	
	private Opinions opinions;
	private LinkedList<AID> juriesWantingToTalk;
	
	private int countVotes;
	
	//	GETTERS
	public Opinions getOpinions() { return opinions; }
	
	public LinkedList<AID> getJuriesWantingToTalk() { return juriesWantingToTalk; }
	
	//	SETTERS
	
	public void setJuriesWantingToTalk(LinkedList<AID> juriesWantingToTalk) { this.juriesWantingToTalk = juriesWantingToTalk; }
	
	@Override
	protected void setup() {
		super.setup();
		
		opinions = new Opinions(NB_JURIES);
		juriesWantingToTalk = new LinkedList<>();
		
		addBehaviour(new WaitingJuries());
		addBehaviour(new OnceAllInnocent());
		addBehaviour(new ReceiveVoteJuries());
		addBehaviour(new ReceiveRequestToVote());
		addBehaviour(new ReceiveArgument());
		addBehaviour(new ReceiveRequestChangeVote());
		addBehaviour(new ReceiveInformationBelief());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	//	CLASSES INTERNES COMPORTEMENT
	/**
	 * Comportement d'attente de l'arrivée et de l'enregistrement des 12 jurés.
	 */
	private class WaitingJuries extends Behaviour {
		private static final long serialVersionUID = -4284246010322048230L;

		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
				sd.setType("jury");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template); 
				juries = new AID[result.length];
				
				if(juries.length == 12)
					sort(result);
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		private void sort(DFAgentDescription[] result) {
			for (int i = 0; i < result.length; ++i) {
				AID jury = result[i].getName();
				switch(jury.getLocalName()) {
					case "Jury1" :
						juries[0] = jury;
						break;
					case "Jury2" : 
						juries[1] = jury;
						break;
					case "Jury3" :
						juries[2] = jury;
						break;
					case "Jury4" :
						juries[3] = jury;
						break;
					case "Jury5" :
						juries[4] = jury;
						break;
					case "Jury6" :
						juries[5] = jury;
						break;
					case "Jury7" :
						juries[6] = jury;
						break;
					case "Jury8" :
						juries[7] = jury;
						break;
					case "Jury9" :
						juries[8] = jury;
						break;
					case "Jury10" :
						juries[9] = jury;
						break;
					case "Jury11" :
						juries[10] = jury;
						break;
					case "Jury12" :
						juries[11] = jury;
						break;
				}
			}
		}

		@Override
		public boolean done() {
			return (juries.length == 12);
		}
		
		@Override
		public int onEnd() {
			myAgent.addBehaviour(new Start(myAgent));
			return super.onEnd();
		}
	}
	
	/**
	 * Comportement de lancement du débat
	 */
	private class Start extends WakerBehaviour {
		private static final long serialVersionUID = 2660076876550906252L;

		public Start(Agent a) { super(a, 10000); }
		
		@Override
		protected void handleElapsedTimeout() {
			// Envoi d'un message à tous les Jury pour leur dire d'être prêt
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			for (int i = 0; i < juries.length; ++i)
				request.addReceiver(juries[i]);
			try {
				request.setContentObject(juries);
				request.setConversationId("juries-ready");
				myAgent.send(request);
			} catch (IOException e) {
				e.printStackTrace();
			}
			addBehaviour(new RequestVoteJuries());
		}
	}
	
	/**
	 * Comportement de demande de vote
	 */
	private class RequestVoteJuries extends OneShotBehaviour {
		private static final long serialVersionUID = -4475110771200834870L;
		
		@Override
		public void action() {
			countVotes = 0;
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			for (int i = 0; i < juries.length; ++i)
				request.addReceiver(juries[i]);
			request.setConversationId("asking-vote");
			myAgent.send(request);
		}
	}
	
	/**
	 * Comportement cyclique de réception des votes des jurés
	 */
	private class ReceiveVoteJuries extends CyclicBehaviour {
		private static final long serialVersionUID = 5363524147069962688L;
		
		private MessageTemplate mt;
		
		public void action() {
			mt = MessageTemplate.MatchConversationId("juries-vote");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					AID jury = reply.getSender();
					int id = getJuriesID(jury);
					opinions.setJuryOpinion(id, Belief.parse(reply.getContent()));
					countVotes++;
					if(countVotes == 12)
						addBehaviour(new InformOpinions());
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement cyclique de réception de la demande de Vote
	 */
	private class ReceiveRequestToVote extends CyclicBehaviour {
		private static final long serialVersionUID = -7996786204438352603L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("request-vote");
			ACLMessage message = myAgent.receive(mt);
			if(message != null) {
				if(message.getPerformative() == ACLMessage.REQUEST) {
					System.out.println(getLocalName() + ":: demande un vote.");
					myAgent.addBehaviour(new RequestVoteJuries());
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un message d'une demande de vote
	 */
	private class ReceiveRequestChangeVote extends CyclicBehaviour {
		private static final long serialVersionUID = -531435194037473825L;
		
		private MessageTemplate mt;
		
		public void action() {
			mt = MessageTemplate.MatchConversationId("change-vote");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					AID jury = reply.getSender();
					int id = getJuriesID(jury);
					opinions.setJuryOpinion(id, Belief.parse(reply.getContent()));
					myAgent.addBehaviour(new InformOpinions());
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un changement d'opinion
	 */
	private class ReceiveInformationBelief extends CyclicBehaviour {
		private static final long serialVersionUID = -1599536764010770608L;
		
		private MessageTemplate mt;
		
		public void action() {
			mt = MessageTemplate.MatchConversationId("inform-belief");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					AID jury = reply.getSender();
					int id = getJuriesID(jury);
					try {
						opinions.setJuryOpinion(id, (Belief) reply.getContentObject());
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement à exécution unique d'envoi à tous les jurés des opinions de tous les jurés
	 */
	private class InformOpinions extends OneShotBehaviour {
		private static final long serialVersionUID = 3344695619379676911L;
		
		public void action() {
			ACLMessage information = new ACLMessage(ACLMessage.INFORM);
			addJuriesToMessage(information);
			try {
				opinions.incrementSent();
				information.setContentObject(opinions);
				System.out.println(opinions);
			} catch (IOException e) {
				e.printStackTrace();
			}
			information.setConversationId("opinions");
			myAgent.send(information);
		}
	}
	
	/**
	 * Comportement à exécution unique de réponse à un {@link Argument}
	 */
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 7188408824906352590L;
		
		private ACLMessage message;
		private Argument argument;
		
		//	CONSTRUCTEURS
		public AnswerToArgument(ACLMessage message) throws UnreadableException {
			this.message = message;
			argument = (Argument) message.getContentObject();
		}

		@Override
		public void action() {
			switch(argument.getId()) {
				case 25:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un {@link Argument}
	 */
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = 3440554003187849924L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("argument");
			ACLMessage message = myAgent.receive(mt);
			int performative;
			if(message != null) {
				try {
					performative = message.getPerformative();
					if(performative == ACLMessage.PROPOSE || performative == ACLMessage.ACCEPT_PROPOSAL)
						myAgent.addBehaviour(new AnswerToArgument(message));
					else if(performative == ACLMessage.REJECT_PROPOSAL)
						myAgent.addBehaviour(new AnswerToReject(message));
					else
						block();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement à exécution unique de réponse à un rejet d'{@link Argument}
	 */
	private class AnswerToReject extends OneShotBehaviour {
		private static final long serialVersionUID = -1808741270435584554L;
		
		private ACLMessage message;
		private Argument argument;
		
		//	CONSTRUCTEURS
		public AnswerToReject(ACLMessage message) throws UnreadableException {
			this.message = message;
			argument = (Argument) message.getContentObject();
		}

		@Override
		public void action() {
			switch(this.argument.getId()) {
				case 15:
					myAgent.addBehaviour(new RequestVoteJuries());
				break;
				case 22:
					myAgent.addBehaviour(new RequestChangeVote(Belief.INNOCENT));
				break;
			}
		}
	}
	
	/**
	 * Comportement exécuté lorsque tous les jurés pensent que l'accusé est innocent
	 */
	private class OnceAllInnocent extends Behaviour {
		private static final long serialVersionUID = 7408682973449826528L;
		
		private boolean done = false;
		
		@Override
		public void action() {
			if(opinions.areAllInnocent()) {
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				request.setConversationId("good-bye");
				addJuriesToMessage(request);
				myAgent.send(request);
				done = true;
			}
		}

		@Override
		public boolean done() { return done; }
	}
}
