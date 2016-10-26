package agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends Jury {
	private static final long serialVersionUID = 4874292225851563156L;

	private AID[] juries;
	
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
			System.out.println("Les Jurés sont tous présents.");
			return super.onEnd();
		}
	}
}
