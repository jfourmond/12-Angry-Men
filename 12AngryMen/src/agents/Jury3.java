package agents;

import java.io.IOException;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

/**
 * Le Jury n°3 est un homme borné, peu importe les arguments il reste persuadé que l'accusé est coupable
 * Il projete son conflit avec son propre fils sur l'affaire
 */
public class Jury3 extends GuiltyFighterJury {
	private static final long serialVersionUID = 4460077013474154584L;

	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new ReceiveArgument());
		addBehaviour(new OnceSecondVote());
		addBehaviour(new OnceAllowedToTalk());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	protected class OnceSecondVote extends Behaviour {
		private static final long serialVersionUID = -3427069231443336483L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return nbVotes == 2; }
		
		@Override
		public int onEnd() {
			addBehaviour(new AskToTalk());
			return super.onEnd();
		}
	}
	
	private class OnceAllowedToTalk extends Behaviour {
		private static final long serialVersionUID = -3784026568834957392L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return allowedToTalk; }
		
		@Override
		public int onEnd() {
			addBehaviour(new ExposeArgument(new Argument(), juries[7]));
			return super.onEnd();
		}
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 7816471759173664266L;
		
		private ACLMessage message;
		private Argument argument;
		
		//	CONSTRUCTEURS
		public AnswerToArgument(ACLMessage message) throws UnreadableException {
			this.message = message;
			argument = (Argument) message.getContentObject();
		}

		@Override
		public void action() {
			ACLMessage reject = null;
			switch(argument.getId()) {
				case 1:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.2);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case 6:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.2);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case 8:	//REJET FAIBLE
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.1);
					addJuriesToMessage(reject);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: WEAK REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
			}
		}
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -4804003371667219349L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("argument");
			ACLMessage message = myAgent.receive(mt);
			int performative;
			if(message != null) {
				try {
					performative = message.getPerformative();
					if(performative == ACLMessage.PROPOSE)
						myAgent.addBehaviour(new AnswerToArgument(message));
					else if(performative == ACLMessage.REJECT_PROPOSAL)
						myAgent.addBehaviour(new AnswerToReject(message));
					else if(performative == ACLMessage.ACCEPT_PROPOSAL)
						// TODO code
						System.err.println("TODO");
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
			switch(this.argument.getId()) {
				case 4:
					myAgent.addBehaviour(new ReviewArgument(message, this.argument));
				break;
			}
		}
	}
}
