import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class TemplateProvider {

   private final String optionalTemplate;
   private final String requiredTemplate;

   public TemplateProvider() {
      try {
         optionalTemplate =
               readTemplateFromResource("optional-getter.java.snippet");
         requiredTemplate =
               readTemplateFromResource("required-getter.java.snippet");
      } catch (final IOException e) {
         throw new IllegalStateException("resource file not readable", e);
      }
   }

   private String readTemplateFromResource(final String resourceName)
         throws IOException {
      return Files
            .readLines(new File(getClass().getClassLoader()
                  .getResource(resourceName).getFile()), Charsets.UTF_8)
            .stream().collect(Collectors.joining("\n"));
   }

   public String getTemplate(final boolean optional) {
      return optional ? optionalTemplate : requiredTemplate;
   }

}
