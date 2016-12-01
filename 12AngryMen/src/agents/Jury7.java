package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Opinions;

public class Jury7 extends NeutralJury {
	private static final long serialVersionUID = 1858012893974526993L;

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
						if(opinions.sent() == 3)
							myAgent.addBehaviour(new ExposeArgument(new Argument(belief()), juries));
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = -666395855036051335L;
		
		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("argument");
			ACLMessage message = myAgent.receive(mt);
			int performative;
			Boolean accepted = null;
			Argument argument;
			if(message != null) {
				try {
					performative = message.getPerformative();
					argument = (Argument) message.getContentObject();
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
