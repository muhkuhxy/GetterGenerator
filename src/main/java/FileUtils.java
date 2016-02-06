import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

public class FileUtils {

   public static List<String> readLines(final String fileName)
         throws IOException {
      return Files.readLines(new File(fileName), Charsets.UTF_8);
   }

   public static void writeLines(final String fileName,
         final List<String> lines) throws IOException {
      Files.write(Joiner.on("\n").join(lines), new File(fileName),
            Charsets.UTF_8);
   }
}
