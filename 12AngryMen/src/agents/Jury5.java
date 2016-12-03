package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

public class Jury5 extends Jury {
	private static final long serialVersionUID = -5443724775577219581L;

	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new ReceiveArgument());
		addBehaviour(new ReceiveJuriesOpinion());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	private class ReceiveJuriesOpinion extends CyclicBehaviour {
		private static final long serialVersionUID = 6417329372143758293L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("opinions");
			ACLMessage message = myAgent.receive(mt);
			if(message != null) {
				if(message.getPerformative() == ACLMessage.INFORM) {
					try {
						Opinions opinions = (Opinions) message.getContentObject();
						if(opinions.sent() == 4) {
							myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 6984704043830365971L;
		
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
				case 2:
					myAgent.addBehaviour(new RejectArgument(message, argument));
				break;
				case 6:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
				case 25:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
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
		private static final long serialVersionUID = -6787394496748513050L;
		
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
				case 9:
					// Demande à Jury1 s'il peut changer son vote en non-coupable
					myAgent.addBehaviour(new RequestChangeVote(Belief.INNOCENT));
				break;
			}
		}
	}
}
