import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.AntPathMatcher;

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

   public static List<String> findFiles(final File parent,
         final List<String> antPatterns) throws IOException {
      final Path parentPath = parent.toPath();
      final List<String> files = new ArrayList<>();
      java.nio.file.Files.walkFileTree(parentPath,
            new PathMatchingVisitor(parentPath, files, antPatterns));
      return files;
   }

   private static final class PathMatchingVisitor
         extends SimpleFileVisitor<Path> {
      private final Path parentPath;
      private final List<String> files;
      private final List<String> antPatterns;
      private final AntPathMatcher matcher = new AntPathMatcher();

      private PathMatchingVisitor(final Path parentPath,
            final List<String> files, final List<String> antPatterns) {
         this.parentPath = parentPath;
         this.files = files;
         this.antPatterns = antPatterns;
      }

      @Override
      public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attrs) throws IOException {
         final String relativePath = parentPath.relativize(file).toString();
         if(antPatterns.stream()
               .anyMatch(pattern -> matcher.match(pattern, relativePath))) {
            files.add(file.toString());
         }
         return super.visitFile(file, attrs);
      }
   }
}
