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
 * Le premier juré à ne pas être certain de la culpabilité de l'accusé
 */
public class Jury8 extends InnocenceDefenseJury {
	private static final long serialVersionUID = 1677578125404393663L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 0.8;
		addBehaviour(new OnceReady());
		addBehaviour(new OnceAllowedToTalk());
		addBehaviour(new ReceiveArgument());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	private class OnceReady extends Behaviour {
		private static final long serialVersionUID = -8482394774543485010L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return ready; }
		
		@Override
		public int onEnd() {
			addBehaviour(new AskToTalk());
			return super.onEnd();
		}
	}
	
	private class OnceAllowedToTalk extends Behaviour {
		private static final long serialVersionUID = -599182665856063880L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return allowedToTalk; }
		
		@Override
		public int onEnd() {
			addBehaviour(new ExposeArgument(new Argument(belief()), juries));
			return super.onEnd();
		}
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -9027696336689844480L;
		
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
						myAgent.addBehaviour(new AnswerToAccept(message));
					else
						block();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else
				block();
		}
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -172427643887737653L;
		
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
				case 4:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.3);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case 5:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.3);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
						myAgent.addBehaviour(new ExposeArgument(new Argument(belief()), juries));
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
			}
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
				case 1:
					myAgent.addBehaviour(new ReviewArgument(message, this.argument, juries));
				break;
				case 2:
					myAgent.addBehaviour(new ReviewArgument(message, this.argument, juries));
				break;
				case 7:
					addBehaviour(new ExposeArgument(new Argument(belief()), juries));
				break;
				case 8:
					if(message.getSender().equals(juries[0]))
						addBehaviour(new ExposeArgument(new Argument(belief()), juries));
				break;
			}
		}
	}
	
	private class AnswerToAccept extends OneShotBehaviour {
		private static final long serialVersionUID = -1389941174321104501L;
		
		private ACLMessage message;
		private Argument argument;
		
		//	CONSTRUCTEURS
		public AnswerToAccept(ACLMessage message) throws UnreadableException {
			this.message = message;
			argument = (Argument) this.message.getContentObject();
		}

		@Override
		public void action() {
			System.out.println(myAgent.getLocalName() + ":: " + argument + " ACCEPTED");
			switch(argument.getId()) {
				case 3:
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					request.setConversationId("request-vote");
					request.addReceiver(juries[0]);
					System.out.println(myAgent.getLocalName() + ":: demande un vote au Jury 1.");
					myAgent.send(request);
				break;
			}
			try {
				addBehaviour(new Influence(message));
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
