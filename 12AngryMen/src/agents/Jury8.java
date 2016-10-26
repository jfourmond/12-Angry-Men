package agents;

import jade.core.Agent;

/**
 * Le premier juré à ne pas être certain de la culpabilité de l'accusé
 */
public class Jury8 extends Agent {
	private static final long serialVersionUID = 1677578125404393663L;

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ":: " + "Arrivée du Jury n°8.");
	}
	
	@Override
	protected void takeDown() {
		System.out.println(getLocalName() + ":: " + "Départ du Jury n°8.");
	}
}
