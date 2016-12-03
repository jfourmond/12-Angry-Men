package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

/**
 * Le premier juré à ne pas être certain de la culpabilité de l'accusé
 */
public class Jury8 extends Jury {
	private static final long serialVersionUID = 1677578125404393663L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = Belief.INNOCENT;
		
		addBehaviour(new ReceiveJuriesOpinion());
		addBehaviour(new ReceiveArgument());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
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
			switch(argument.getId()) {
				case 4:
					myAgent.addBehaviour(new RejectArgument(message, argument));
				break;
				case 5:
					myAgent.addBehaviour(new RejectArgument(message, argument));
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 14:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 25:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
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
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 8:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 16:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 18:
					myAgent.addBehaviour(new ReviewArgument(message, argument, juries));
				break;
				case 19:
					myAgent.addBehaviour(new ReviewArgument(message, argument, juries));
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
			switch(argument.getId()) {
				case 3:
					myAgent.addBehaviour(new AskVote());
				break;
			}
			try {
				addBehaviour(new Influence(message));
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ReceiveJuriesOpinion extends CyclicBehaviour {
		private static final long serialVersionUID = 5550813940577575102L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("opinions");
			ACLMessage message = myAgent.receive(mt);
			if(message != null) {
				if(message.getPerformative() == ACLMessage.INFORM) {
					try {
						Opinions opinions = (Opinions) message.getContentObject();
						switch(opinions.sent()) {
							case 1:
								addBehaviour(new ExposeArgument(new Argument(belief), juries));
							break;
							case 5:
								myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
							break;
						}
							
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
}
