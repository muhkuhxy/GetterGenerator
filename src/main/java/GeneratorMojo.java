import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.util.AntPathMatcher;

@Mojo(name = "generateGetters")
public class GeneratorMojo extends AbstractMojo {

   @Parameter(required = true,
         defaultValue = "${project.build.sourceDirectory}")
   private File sourceDirectory;
   @Parameter(defaultValue = "\n\n")
   private String delimiter;
   @Parameter(defaultValue = "")
   private String suffix;
   @Parameter(defaultValue = "GENERATE_GETTERS")
   private String placeholder;
   @Parameter
   private List<String> includes;

   private Replacer replacer;
   private GetterMaker maker;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      getLog().info("generating getters");
      replacer = new Replacer(placeholder);
      maker = new GetterMaker(suffix);
      if (includes == null) {
         includes = new ArrayList<>();
      }
      if (includes.isEmpty()) {
         includes.add("**/*.java");
      }
      final List<String> files = new ArrayList<>();
      try {
         Files.walkFileTree(sourceDirectory.toPath(),
               new SimpleFileVisitor<Path>() {
                  private final AntPathMatcher matcher = new AntPathMatcher();

                  @Override
                  public FileVisitResult visitFile(final Path file,
                        final BasicFileAttributes attrs) throws IOException {
                     final String path = file.toString();
                     if (includes.stream().anyMatch(pattern -> {
                        return matcher.match(pattern, path);
                     })) {
                        files.add(path);
                     }
                     return super.visitFile(file, attrs);
                  }
               });
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      getLog().info(files.size() + " files found: " + files);

      files.forEach(this::analyzeFileAndReplaceOrPrintGetters);
   }

   private void analyzeFileAndReplaceOrPrintGetters(final String file) {
      final String getters =
            getters(file).stream().collect(Collectors.joining(delimiter));
      replacer.replaceIn(file, getters);
   }

   private List<String> getters(final String file) {
      final List<GetterSpec> specs = new GetterAnalyzer(file).analyze();
      return specs.stream().map(maker::make).collect(Collectors.toList());
   }

}
