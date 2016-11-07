package agents;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends Jury {
	private static final long serialVersionUID = 4874292225851563156L;
	
	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new WaitingJuries());
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
	
	//	CLASSES INTERNES COMPORTEMENT
	/**
	 * Comportement d'attente de l'arrivée et de l'enregistrement des 12 jurés.
	 */
	private class WaitingJuries extends Behaviour {
		private static final long serialVersionUID = -4284246010322048230L;

		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
				sd.setType("jury");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template); 
				juries = new AID[result.length];
				for (int i = 0; i < result.length; ++i)
					juries[i] = result[i].getName();
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		@Override
		public boolean done() {
			return (juries.length == 12);
		}
		
		@Override
		public int onEnd() {
			// Envoi d'un message à tous les Jury pour leur dire d'être prêt et de "commencer" leurs comportements
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			for (int i = 0; i < juries.length; ++i)
				request.addReceiver(juries[i]);
			try {
				request.setContentObject(juries);
				request.setConversationId("juries-ready");
				myAgent.send(request);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return super.onEnd();
		}
	}
}
