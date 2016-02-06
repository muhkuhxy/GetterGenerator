import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateProvider {

   private final String optionalTemplate;
   private final String requiredTemplate;

   public TemplateProvider() {
      try {
         optionalTemplate =
               readTemplateFromResource("optional-getter.java.snippet");
         requiredTemplate =
               readTemplateFromResource("required-getter.java.snippet");
      } catch(final IOException e) {
         throw new IllegalStateException("resource file not readable", e);
      }
   }

   private String readTemplateFromResource(final String resourceName)
         throws IOException {
      final List<String> lines = readResource(resourceName);
      return lines.stream().collect(Collectors.joining("\n"));
   }

   private List<String> readResource(final String resourceName)
         throws IOException {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(
            getClass().getClassLoader().getResourceAsStream(resourceName)));
      final List<String> lines = new ArrayList<>();
      String line;
      while((line = reader.readLine()) != null) {
         lines.add(line);
      }
      return lines;
   }

   public String getTemplate(final boolean optional) {
      return optional ? optionalTemplate : requiredTemplate;
   }

}
