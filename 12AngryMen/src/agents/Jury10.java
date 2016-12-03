package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Opinions;

public class Jury10 extends Jury {
	private static final long serialVersionUID = 5550699255797694772L;

	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new ReceiveJuriesOpinion());
		addBehaviour(new ReceiveArgument());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -5708986610042651190L;
		
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
				case 7:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 9:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 15:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 26:
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
					if(performative == ACLMessage.PROPOSE)
						myAgent.addBehaviour(new AnswerToArgument(message));
					else
						block();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else
				block();
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
						if(opinions.sent() == 8)
							myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
}
