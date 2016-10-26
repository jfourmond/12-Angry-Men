package agents;

import jade.core.Agent;

/**
 * Le Jury n°3 est un homme borné, peu importe les arguments il reste persuadé que l'accusé est coupable
 * Il projete son conflit avec son propre fils sur l'affaire
 */
public class Jury3 extends Agent {
	private static final long serialVersionUID = 4460077013474154584L;

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ":: " + "Arrivée du Jury n°3.");
	}
	
	@Override
	protected void takeDown() {
		System.out.println(getLocalName() + ":: " + "Départ du Jury n°3.");
	}
	
}
