package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

public class Jury2 extends NeutralJury {
	private static final long serialVersionUID = -8216810411937057523L;

	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new ReceiveArgument());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -8265015153695403194L;
		
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
				case 1:
					
				break;
			}
		}
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = 8689827255443396798L;
		
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
