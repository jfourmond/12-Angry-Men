package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;

public class Jury11 extends Jury {
	private static final long serialVersionUID = -4948709326560541429L;

	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new ReceiveArgument());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	/**
	 * Comportement à exécution unique de réponse à un {@link Argument}
	 */
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = 6358084470390145144L;
		
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
				case 10:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
					myAgent.addBehaviour(new ExposeArgument(new Argument(Belief.INNOCENT), juries));
				break;
				case 13:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 15:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un {@link Argument}
	 */
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -6871955334914965804L;
		
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
