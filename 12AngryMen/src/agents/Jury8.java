package agents;

/**
 * Le premier juré à ne pas être certain de la culpabilité de l'accusé
 */
public class Jury8 extends Jury {
	private static final long serialVersionUID = 1677578125404393663L;

	@Override
	protected void setup() {
		super.setup();
		
		belief = 1.0;
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
	}
}
