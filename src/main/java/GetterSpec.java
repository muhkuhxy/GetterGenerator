
public class GetterSpec {

	private final String type;
	private final String field;
	private final String className;
	private final boolean optional;

	public String getType() {
		return type;
	}

	public String getField() {
		return field;
	}

	public boolean isOptional() {
		return optional;
	}


	public String getClassName() {
		return className;
	}

	public GetterSpec(final String type, final String field, final String className, final boolean optional) {
		this.type = type;
		this.field = field;
		this.className = className;
		this.optional = optional;
	}

	@Override
	public String toString() {
		return "GetterSpec [type=" + type + ", field=" + field + ", className=" + className + ", optional=" + optional
				+ "]";
	}

	public String toJson()
	{
		final String format = String.format("{\"className\":\"%s\",\"field\":\"%s\",\"type\":\"%s\",\"optional\":%s}",
				className, field, type, optional );
		System.out.println(format);
		return format;
	}

}
