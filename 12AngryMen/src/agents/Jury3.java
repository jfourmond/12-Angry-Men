package agents;

/**
 * Le Jury n°3 est un homme borné, peu importe les arguments il reste persuadé que l'accusé est coupable
 * Il projete son conflit avec son propre fils sur l'affaire
 */
public class Jury3 extends Jury {
	private static final long serialVersionUID = 4460077013474154584L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 0.0;
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
}
