package agents;

import java.io.IOException;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import metiers.Argument;

/**
 * Le premier juré à ne pas être certain de la culpabilité de l'accusé
 */
public class Jury8 extends InnocenceDefenseJury {
	private static final long serialVersionUID = 1677578125404393663L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 1.0;
		addBehaviour(new OnceReady());
		addBehaviour(new OnceAllowedToTalk());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	protected class OnceReady extends Behaviour {
		private static final long serialVersionUID = -8482394774543485010L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return ready; }
		
		@Override
		public int onEnd() {
			addBehaviour(new AskToTalk());
			return super.onEnd();
		}
	}
	
	protected class ExposeDoubt extends OneShotBehaviour {
		private static final long serialVersionUID = -2925922941582026625L;

		@Override
		public void action() {
			Argument arg = new Argument();
			ACLMessage doubt = new ACLMessage(ACLMessage.PROPOSE);
			addJuriesToMessage(doubt);
			try {
				doubt.setContentObject(arg);
				doubt.setConversationId("argument");
				myAgent.send(doubt);
				System.out.println(myAgent.getLocalName() + ":: expose ses doutes (" + arg + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class OnceAllowedToTalk extends Behaviour {
		private static final long serialVersionUID = -599182665856063880L;

		@Override
		public void action() { }

		@Override
		public boolean done() { return allowedToTalk; }
		
		@Override
		public int onEnd() {
			addBehaviour(new ExposeDoubt());
			return super.onEnd();
		}
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
