package agents;

import metiers.Argument;
import metiers.Belief;

/**
 * Jury défendant l'innocence, ou une fois avoir changer d'avis (coupable à innocent), reste innocent
 */
public abstract class InnocenceDefenseJury extends Jury {
	private static final long serialVersionUID = -4265533315522210511L;
	
	@Override
	public void influence(Argument argument) {
		if(belief() == Belief.GUILTY)
			belief += argument.getStrength();
	}
}
