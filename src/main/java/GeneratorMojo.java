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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.util.AntPathMatcher;

@Mojo(name = "generateGetters", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorMojo extends AbstractMojo {

   @Parameter(required = true,
         defaultValue = "${project.build.sourceDirectory}")
   private File directory;
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
      replacer = new Replacer(placeholder);
      maker = new GetterMaker(suffix == null ? "" : suffix);
      final Log log = getLog();
      log.info("generating getters");
      if(includes == null) {
         includes = new ArrayList<>();
      }
      if(includes.isEmpty()) {
         includes.add("**/*.java");
      }
      log.debug(includes.toString());
      log.debug(directory.toString());
      final List<String> files = getFiles(log);
      files.forEach(this::analyzeFileAndReplaceOrPrintGetters);
   }

   private List<String> getFiles(final Log log) {
      final List<String> files = new ArrayList<>();
      try {

         final Path directoryAsPath = directory.toPath();
         Files.walkFileTree(directoryAsPath, new SimpleFileVisitor<Path>() {
            private final AntPathMatcher matcher = new AntPathMatcher();

            @Override
            public FileVisitResult visitFile(final Path file,
                  final BasicFileAttributes attrs) throws IOException {
               final String relativePath =
                     directoryAsPath.relativize(file).toString();
               log.debug("matching " + relativePath);
               if(includes.stream().anyMatch(pattern -> {
                  return matcher.match(pattern, relativePath);
               })) {
                  log.debug("adding " + relativePath);
                  files.add(file.toString());
               }
               return super.visitFile(file, attrs);
            }
         });
      } catch(final IOException e) {
         throw new RuntimeException(e);
      }

      log.info("will process " + files.size() + " files");
      return files;
   }

   private void analyzeFileAndReplaceOrPrintGetters(final String file) {
      final String getters =
            getters(file).stream().collect(Collectors.joining(delimiter));
      replacer.replaceIn(file, getters);
   }

   private List<String> getters(final String file) {
      getLog().info("processing " + file);
      final List<GetterSpec> specs = new GetterAnalyzer(file).analyze();
      return specs.stream().map(maker::make).collect(Collectors.toList());
   }

}
