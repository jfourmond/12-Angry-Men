package agents;

import jade.core.Agent;

/**
 * Le Jury n°1 est le président / l'arbitre des jurés
 *
 */
public class Jury1 extends Agent {
	private static final long serialVersionUID = 4874292225851563156L;

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ":: " + "Arrivée du Jury n°1.");
	}
	
	@Override
	protected void takeDown() {
		System.out.println(getLocalName() + ":: " + "Départ du Jury n°1.");
	}
}
