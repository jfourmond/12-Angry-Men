package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;

public class Jury9 extends InnocenceDefenseJury {
	private static final long serialVersionUID = -8550688594260356089L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 0.5;
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
				case 3:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
				case 6:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
					myAgent.addBehaviour(new ExposeArgument(new Argument(Belief.INNOCENT), juries));
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
}
