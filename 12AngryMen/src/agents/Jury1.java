package agents;

import java.io.IOException;
import java.util.LinkedList;

import jade.core.AID;
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
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends NeutralJury {
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
		addBehaviour(new ReceiveVoteJuries());
		addBehaviour(new ReceiveRequestToTalk());
		addBehaviour(new ReceiveRequestToVote());
		addBehaviour(new ReceiveArgument());
		addBehaviour(new ReceiveRequestChangeVote());
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
			addBehaviour(new AskVoteJuries());
			return super.onEnd();
		}
	}
	
	/**
	 * Comportement de demande de vote
	 */
	private class AskVoteJuries extends OneShotBehaviour {
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
					System.out.println("Vote " + jury.getLocalName() + " -> " + opinions.getJuryOpinion(id));
					if(countVotes == 12) {
						AID juryWantingToTalk = juriesWantingToTalk.removeFirst();
						addBehaviour(new AllowToTalk(juryWantingToTalk));
					}
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
					myAgent.addBehaviour(new AskVoteJuries());
				}
			} else
				block();
		}
	}
	
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
					System.out.println("Vote " + jury.getLocalName() + " -> " + opinions.getJuryOpinion(id));
					myAgent.addBehaviour(new InformOpinions());
				}
			} else
				block();
		}
	}
	
	private class InformOpinions extends OneShotBehaviour {
		private static final long serialVersionUID = 3344695619379676911L;
		
		public void action() {
			ACLMessage information = new ACLMessage(ACLMessage.INFORM);
			addJuriesToMessage(information);
			information.setConversationId("opinions");
			myAgent.send(information);
		}
	}
	
	/**
	 * Comportement cyclique de réception de la demande de... Discussion (?)
	 */
	private class ReceiveRequestToTalk extends CyclicBehaviour {
		private static final long serialVersionUID = -6552752738688238433L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("asking-to-talk");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					AID jury = reply.getSender();
					System.out.println(jury.getLocalName() + " veut parler.");
					juriesWantingToTalk.add(jury);
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement d'autorisation à parler
	 */
	private class AllowToTalk extends OneShotBehaviour {
		private static final long serialVersionUID = 1535672212649085802L;
		
		private AID jury;
		
		public AllowToTalk(AID jury) {
			this.jury = jury;
		}

		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage auto = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			auto.addReceiver(jury);
			auto.setConversationId("allowing-to-talk");
			myAgent.send(auto);
		}
	}
	
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
				case 3:
				break;
			}
		}
	}
	
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
			ACLMessage reject = null;
			switch(this.argument.getId()) {
				case 8:	// REJET DU REJET
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.giveStrength(0.2);
					addJuriesToMessage(reject);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
			}
		}
	}
}
