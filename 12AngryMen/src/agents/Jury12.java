package agents;

import java.io.IOException;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

public class Jury12 extends NeutralJury {
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
			ACLMessage reject = null;
			switch(argument.getId()) {
				case 11:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.3);
					addJuriesToMessage(reject);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case 12:
					reject = message.createReply();
					reject.setPerformative(ACLMessage.REJECT_PROPOSAL);
					argument.removeStrength(0.3);
					try {
						reject.setContentObject(argument);
						System.out.println(myAgent.getLocalName() + ":: REJECT " + argument);
						myAgent.send(reject);
						myAgent.addBehaviour(new ExposeArgument(new Argument(belief()), juries));
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
			}
		}
	}
	
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
					if(performative == ACLMessage.PROPOSE || performative == ACLMessage.REJECT_PROPOSAL || performative == ACLMessage.ACCEPT_PROPOSAL)
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
