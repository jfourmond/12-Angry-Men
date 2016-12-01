package metiers;

public enum Belief {
	INNOCENT,
	GUILTY;
	
	public static Belief parse(String string) {
		switch(string) {
			case "INNOCENT" :
				return INNOCENT;
			case "GUILTY" :
				return GUILTY;
			default:
				return null;
		}
	}
	
}
