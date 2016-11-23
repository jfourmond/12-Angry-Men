package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

public class Jury6 extends NeutralJury {
	private static final long serialVersionUID = -3831777205301214367L;

	@Override
	protected void setup() {
		super.setup();
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	private class AnswerToArgument extends OneShotBehaviour {
		private static final long serialVersionUID = -2805433556211668554L;
		
		private Argument argument;
		private Boolean accepted;
		
		//	CONSTRUCTEURS
		public AnswerToArgument(Argument argument, Boolean accepted) {
			this.argument = argument;
			this.accepted = accepted;
		}

		@Override
		public void action() { }
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -4804003371667219349L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("argument");
			ACLMessage reply = myAgent.receive(mt);
			int performative;
			Boolean accepted = null;
			Argument argument;
			if(reply != null) {
				try {
					performative = reply.getPerformative();
					argument = (Argument) reply.getContentObject();
					if(performative == ACLMessage.PROPOSE) {
						accepted = null;
						myAgent.addBehaviour(new AnswerToArgument(argument, accepted));
					} else if(performative == ACLMessage.ACCEPT_PROPOSAL) {
						accepted = true;
						myAgent.addBehaviour(new AnswerToArgument(argument, accepted));
					} else if(performative == ACLMessage.REJECT_PROPOSAL) {
						accepted = false;
						myAgent.addBehaviour(new AnswerToArgument(argument, accepted));
					} else
						block();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else
				block();
		}
	}
}
