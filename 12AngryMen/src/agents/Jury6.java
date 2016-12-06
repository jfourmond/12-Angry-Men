package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

public class Jury6 extends Jury {
	private static final long serialVersionUID = -3831777205301214367L;

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
		private static final long serialVersionUID = -42618592389592504L;
		
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
				case 17:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
					myAgent.addBehaviour(new ProposeVote(myAgent));
				break;
				case 25:
					myAgent.addBehaviour(new AcceptArgument(message, argument));
				break;
			}
		}
	}
	
	/**
	 * Comportement cyclique de réception d'un {@link Argument}
	 */
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = 2164912534474908058L;
		
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
	
	/**
	 * Comportement demandant une vote (au {@link Jury1}) après 1 secondes
	 */
	protected class ProposeVote extends WakerBehaviour {
		private static final long serialVersionUID = 4260311352797852824L;

		public ProposeVote(Agent a) { super(a, 1000); }
		
		@Override
		protected void handleElapsedTimeout() { myAgent.addBehaviour(new AskVote()); }	
	}
}
