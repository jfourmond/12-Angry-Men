package agents;

import java.io.IOException;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

public class Jury10 extends GuiltyFighterJury {
	private static final long serialVersionUID = 5550699255797694772L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 0.0;
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
			ACLMessage reject = null;
			switch(argument.getId()) {
				case 7:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(1.0);	// Rejet très fort
					addJuriesToMessage(reject);
					try {
						reject.setContentObject(argument);
						myAgent.send(reject);
						System.out.println(myAgent.getLocalName() + ":: STRONG REJECT " + argument);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case 9:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.2);
					addJuriesToMessage(reject);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
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
					if(performative == ACLMessage.PROPOSE || performative == ACLMessage.ACCEPT_PROPOSAL || performative == ACLMessage.REJECT_PROPOSAL)
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
