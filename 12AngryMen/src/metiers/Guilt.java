package metiers;

public enum Guilt {
	INNOCENT,
	GUILTY;
	
	public static Guilt parse(String string) {
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
