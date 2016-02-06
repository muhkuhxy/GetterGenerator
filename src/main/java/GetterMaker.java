public class GetterMaker {

   private static TemplateProvider templateProvider = new TemplateProvider();

   private final String fieldNameSuffix;

   public GetterMaker(final String suffix) {
      this("get", suffix);
   }

   public GetterMaker(final String getterPrefix, final String fieldNameSuffix) {
      this.fieldNameSuffix = fieldNameSuffix;
   }

   public String make(final GetterSpec spec) {
      final String template = templateProvider.getTemplate(spec.isOptional());
      final String getterName = fieldToGetterName(spec.getField());
      return String.format(template, spec.getType(), getterName,
            spec.getField(), spec.getClassName());
   }

   private String fieldToGetterName(final String name) {
      return "get" + name.substring(0, 1).toUpperCase()
            + name.substring(1, name.endsWith(fieldNameSuffix)
                  ? name.length() - fieldNameSuffix.length() : name.length());
   }

}
