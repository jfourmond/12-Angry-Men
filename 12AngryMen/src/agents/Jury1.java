package agents;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
		addBehaviour(new ReceiveVoteJuries());
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
				
				if(juries.length == 12)
					sort(result);
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		private void sort(DFAgentDescription[] result) {
			for (int i = 0; i < result.length; ++i) {
				AID jury = result[i].getName();
				switch(jury.getLocalName()) {
					case "Jury1" :
						juries[0] = jury;
						break;
					case "Jury2" : 
						juries[1] = jury;
						break;
					case "Jury3" :
						juries[2] = jury;
						break;
					case "Jury4" :
						juries[3] = jury;
						break;
					case "Jury5" :
						juries[4] = jury;
						break;
					case "Jury6" :
						juries[5] = jury;
						break;
					case "Jury7" :
						juries[6] = jury;
						break;
					case "Jury8" :
						juries[7] = jury;
						break;
					case "Jury9" :
						juries[8] = jury;
						break;
					case "Jury10" :
						juries[9] = jury;
						break;
					case "Jury11" :
						juries[10] = jury;
						break;
					case "Jury12" :
						juries[11] = jury;
						break;
				}
			}
		}

		@Override
		public boolean done() {
			return (juries.length == 12);
		}
		
		@Override
		public int onEnd() {
			// Envoi d'un message à tous les Jury pour leur dire d'être prêt
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
			addBehaviour(new AskVoteJuries());
			return super.onEnd();
		}
	}
	
	/**
	 * Comportement de demande de vote
	 */
	private class AskVoteJuries extends OneShotBehaviour {
		private static final long serialVersionUID = -4475110771200834870L;

		@Override
		public void action() {
			// Envoi d'un message à tous les Jury pour leur dire d'être prêt
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			for (int i = 0; i < juries.length; ++i)
				request.addReceiver(juries[i]);
			request.setConversationId("asking-vote");
			myAgent.send(request);
		}
		
	}
	
	/**
	 * Comportement cyclique de réception des votes des jurés
	 */
	private class ReceiveVoteJuries extends CyclicBehaviour {
		private static final long serialVersionUID = 5363524147069962688L;
		
		private MessageTemplate mt;
		// private Guilt votes[];
		
		public void action() {
			mt = MessageTemplate.MatchConversationId("juries-vote");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					System.out.println(reply.getSender().getLocalName() + ":: " + reply.getContent());
					
				}
			} else
				restart();
		}
	}
}
