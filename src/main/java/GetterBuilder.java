import java.util.ArrayList;
import java.util.List;

public class GetterBuilder {

	private final List<String> getters = new ArrayList<>();

	public void addMember( final boolean nullable, final String type, final List< String > names )
	{
		names.forEach(n -> {
			getters.add( makeMethod(nullable, type, n) );
		} );
	}

	private String makeMethod(final boolean nullable, final String type, final String n) {

		return makeMethodSignature( nullable, type, n ) +
									makeMethodBody( nullable, n );
	}

	private String makeMethodBody(final boolean nullable, final String n) {
		String body;
		if(nullable) {
			body = "return Optional.ofNullable( "+n+" )";
		}
		else {
			body = "Preconditions.checkNotNull( %1s, \"%1s\" );";
		}

		return body;
	}

	private String makeMethodSignature(final boolean nullable, final String type, final String name) {
		return String.format( "   public %s %s()", makeReturnType(nullable, type), fieldToGetterName(name) );
	}

	private String makeReturnType(final boolean nullable, final String type) {
		return String.format( nullable ? "Optional< %s >" : "%s", type );
	}

	private static String fieldToGetterName(final String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.endsWith("_") ? name.length() - 1 : name.length());
	}

}
