package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;

public class Jury12 extends Jury {
	private static final long serialVersionUID = 2463325118992257112L;

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
		private static final long serialVersionUID = 1962341225762276021L;
		
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
				case 11:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 12:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 24:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
				case 26:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un {@link Argument}
	 */
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -7329105425008293854L;
		
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
					else if(performative == ACLMessage.REJECT_PROPOSAL )
						myAgent.addBehaviour(new AnswerToReject(message));
					else block();
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
		private static final long serialVersionUID = -5773576052319418263L;
		
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
				case 22:
					setBelief(Belief.INNOCENT);
					myAgent.addBehaviour(new RequestChangeVote(belief));
				break;
			}
		}
	}
}
