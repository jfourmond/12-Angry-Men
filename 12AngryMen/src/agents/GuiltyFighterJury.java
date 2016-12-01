package agents;

import metiers.Argument;
import metiers.Belief;

/**
 * Influencé que faiblement par les arguments sur l'innocence du coupable
 */
public abstract class GuiltyFighterJury extends Jury {
	private static final long serialVersionUID = -1701599054716035552L;
	@Override
	public void influence(Argument argument) {
		if(belief() == Belief.GUILTY)
			// TODO A modifier
			belief += argument.getStrength() / 2;
	}
}
