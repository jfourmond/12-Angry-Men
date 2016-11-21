package agents;

import java.io.IOException;
import java.util.LinkedList;

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
import metiers.Guilt;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends Jury {
	private static final long serialVersionUID = 4874292225851563156L;
	
	private Guilt opinions[];
	private LinkedList<AID> juriesWantingToTalk;
	
	//	GETTERS
	public Guilt[] getOpinions() { return opinions; }
	
	public LinkedList<AID> getJuriesWantingToTalk() { return juriesWantingToTalk; }
	
	//	SETTERS
	public void setOpinions(Guilt[] opinions) { this.opinions = opinions; }
	
	public void setJuriesWantingToTalk(LinkedList<AID> juriesWantingToTalk) { this.juriesWantingToTalk = juriesWantingToTalk; }
	
	@Override
	protected void setup() {
		super.setup();
		
		opinions = new Guilt[NB_JURIES];
		juriesWantingToTalk = new LinkedList<>();
		
		addBehaviour(new WaitingJuries());
		addBehaviour(new ReceiveVoteJuries());
		addBehaviour(new ReceiveRequestToTalk());
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
		private int countVotes;
		// private Guilt votes[];
		
		@Override
		public void onStart() {
			countVotes = 0;
		}
		
		public void action() {
			mt = MessageTemplate.MatchConversationId("juries-vote");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					AID jury = reply.getSender();
					int id = getJuriesID(jury);
					opinions[id-1] = Guilt.parse(reply.getContent());
					countVotes++;
					System.out.println("Vote " + jury.getLocalName() + " -> " + opinions[id-1]);
					if(countVotes == 12) {
						AID juryWantingToTalk = juriesWantingToTalk.removeFirst();
						addBehaviour(new AllowToTalk(juryWantingToTalk));
					}
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement cyclique de réception de la demande de... Discussion (?)
	 */
	private class ReceiveRequestToTalk extends CyclicBehaviour {
		private static final long serialVersionUID = -6552752738688238433L;

		private MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("asking-to-talk");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.REQUEST) {
					AID jury = reply.getSender();
					System.out.println(jury.getLocalName() + " veut parler.");
					juriesWantingToTalk.add(jury);
				}
			} else
				block();
		}
	}
	
	/**
	 * Comportement d'autorisation à parler
	 */
	private class AllowToTalk extends OneShotBehaviour {
		private static final long serialVersionUID = 1535672212649085802L;
		
		private AID jury;
		
		public AllowToTalk(AID jury) {
			this.jury = jury;
		}

		@Override
		public void action() {
			// Envoi de la demande au Jury 1
			ACLMessage auto = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			auto.addReceiver(jury);
			auto.setConversationId("allowing-to-talk");
			myAgent.send(auto);
		}
	}
}
