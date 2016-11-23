package agents;

import java.io.IOException;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

/**
 * Le Jury n°3 est un homme borné, peu importe les arguments il reste persuadé que l'accusé est coupable
 * Il projete son conflit avec son propre fils sur l'affaire
 */
public class Jury3 extends GuiltyFighterJury {
	private static final long serialVersionUID = 4460077013474154584L;

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
					ACLMessage reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.2);
					try {
						reject.setContentObject(argument);
						myAgent.send(reject);
						System.out.println(myAgent.getLocalName() + ":: refuse " + argument);
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
