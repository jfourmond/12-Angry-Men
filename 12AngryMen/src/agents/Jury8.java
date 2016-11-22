package agents;

import java.io.IOException;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
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
			Argument arg = new Argument(0.1);
			ACLMessage request = new ACLMessage(ACLMessage.PROPOSE);
			for (int i = 0; i < juries.length; ++i)
				request.addReceiver(juries[i]);
			try {
				request.setContentObject(arg);
				request.setConversationId("doubt_jury8");
				myAgent.send(request);
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
}
