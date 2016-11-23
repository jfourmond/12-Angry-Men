package agents;

import metiers.Argument;
import metiers.Guilt;

/**
 * Influencé que faiblement par les arguments sur l'innocence du coupable
 */
public abstract class GuiltyFighterJury extends Jury {
	private static final long serialVersionUID = -1701599054716035552L;
	@Override
	public void influence(Argument argument) {
		if(belief() == Guilt.GUILTY)
			// TODO A modifier
			belief += argument.getStrength() / 2;
	}
}
