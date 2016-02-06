import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

   public static void main(final String[] args) throws IOException {
      final String gettersPerFile = Arrays.stream(args).map(f -> {
         final String getters =
               new GetterAnalyzer(f).analyze().stream().map(GetterSpec::toJson)
               .collect(Collectors.joining(", ", "[", "]"));
         return "\"" + f + "\": " + getters;
      }).collect(Collectors.joining(", ", "{", "}"));
      System.out.println(gettersPerFile);
   }

}
