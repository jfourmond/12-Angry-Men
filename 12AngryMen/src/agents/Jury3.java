package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

/**
 * Le Jury n°3 est un homme borné, peu importe les arguments il reste persuadé que l'accusé est coupable
 * Il projete son conflit avec son propre fils sur l'affaire
 */
public class Jury3 extends Jury {
	private static final long serialVersionUID = 4460077013474154584L;

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

	/**
	 * Comportement à exécution unique de réponse à un {@link Argument}
	 */
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
			switch(argument.getId()) {
				case 1:
					myAgent.addBehaviour(new RejectArgument(message, argument));
				break;
				case 6:
					myAgent.addBehaviour(new RejectArgument(message, argument));
				break;
				case 8:	//REJET FAIBLE
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 17: // REJET FAIBLE ET DISCUTABLE
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 21:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 22:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 23:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 25:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
				break;
				case 26:
					myAgent.addBehaviour(new RejectArgument(message, argument, juries));
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
					myAgent.addBehaviour(new Concede(myAgent));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un {@link Argument}
	 */
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
					else
						block();
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
				case 11:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
				case 13:
					myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception des opinions des {@link Jury}s
	 */
	private class ReceiveJuriesOpinion extends CyclicBehaviour {
		private static final long serialVersionUID = 265114511058710632L;
		
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
							case 2:
								addBehaviour(new ExposeArgument(new Argument(belief), juries));
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
	
	/**
	 * Comportement de changement d'opinion suite à une conciliation
	 */
	protected class Concede extends WakerBehaviour {
		private static final long serialVersionUID = 369773853166098717L;

		public Concede(Agent a) { super(a, 2500); }
		
		@Override
		protected void handleElapsedTimeout() {
			myAgent.addBehaviour(new RequestChangeVote(Belief.INNOCENT));
		}
		
	}
}
