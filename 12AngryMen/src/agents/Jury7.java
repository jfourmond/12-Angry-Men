package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;
import metiers.Belief;
import metiers.Opinions;

public class Jury7 extends Jury {
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
		private static final long serialVersionUID = -6347777542010015399L;
		
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
				case 20:
					
				break;
			}
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
						if(opinions.sent() == 3)
							myAgent.addBehaviour(new ExposeArgument(new Argument(belief), juries));
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			} else
				block();
		}
	}
	
	private class ReceiveArgument extends CyclicBehaviour {
		private static final long serialVersionUID = 4064065669188876299L;
		
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
	
	private class AnswerToReject extends OneShotBehaviour {
		private static final long serialVersionUID = -8713547660008022539L;
		
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
					myAgent.addBehaviour(new RequestChangeVote(Belief.INNOCENT));
				break;
			}
		}
	}
}
