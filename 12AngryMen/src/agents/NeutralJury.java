package agents;

import metiers.Argument;

/**
 * {@link Jury} dont les croyances evolue moyennement sur tous les arguments
 */
public abstract class NeutralJury extends Jury{
	private static final long serialVersionUID = 708540974223830352L;
	
	@Override
	public void influence(Argument argument) {
			belief += argument.getStrength() / 2;
	}
}
