import java.io.File;
import java.io.IOException;
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
      if(includes == null || includes.isEmpty()) {
         includes = new ArrayList<>();
         includes.add("**/*.java");
      }
      log.debug(includes.toString());
      log.debug(directory.toString());
      final List<String> files = getFiles(log);
      files.forEach(this::analyzeFileAndReplaceGetters);
   }

   private List<String> getFiles(final Log log) {
      final List<String> files;
      try {
         files = FileUtils.findFiles(directory, includes);
      } catch(final IOException e) {
         throw new RuntimeException(e);
      }
      log.info("will process " + files.size() + " file(s)");
      return files;
   }

   private void analyzeFileAndReplaceGetters(final String file) {
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
