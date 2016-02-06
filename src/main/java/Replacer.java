import java.io.IOException;
import java.util.List;

public class Replacer {

   private final String placeholder;
   private int from;
   private int to;
   private String fileName;
   private String replacement;
   private List<String> lines;

   public Replacer(final String placeholder) {
      this.placeholder = placeholder;
   }

   public void replaceIn(final String fileName, final String replacement) {
      try {
         unsafeReplace(fileName, replacement);
      } catch(final IOException e) {
         throw new IllegalArgumentException("error replacing in file", e);
      }
   }

   private void unsafeReplace(final String fileName, final String replacement)
         throws IOException {
      init(fileName, replacement);
      findPlaceholdersAndClearContainedLines();
      writeReplacementBetweenPlaceholders();
   }

   private void init(final String fileName, final String replacement) throws IOException {
      this.fileName = fileName;
      this.replacement = replacement;
      from = -1;
      to = -1;
      lines = FileUtils.readLines(this.fileName);
   }

   private void findPlaceholdersAndClearContainedLines() {
      for(int i = this.lines.size() - 1; i >= 0; i--) {
         if(this.lines.get(i).contains(placeholder)) {
            if(to == -1) {
               to = i;
               continue;
            } else if(from == -1) {
               from = i + 1;
               break;
            }
         }
         if(to != -1) {
            this.lines.remove(i);
         }
      }
      assertValidNumberOfPlaceholders();
   }

   private void writeReplacementBetweenPlaceholders() throws IOException {
      lines.add(from, replacement);
      FileUtils.writeLines(this.fileName, lines);
   }

   private void assertValidNumberOfPlaceholders() {
      if(!noPlaceholders() && atLeastOnePlaceholder()) {
         throw new IllegalStateException(this.fileName
               + " contains only one marker. Hint: place markers on consecutive lines initially.");
      }
   }

   private boolean atLeastOnePlaceholder() {
      return from == -1 || to == -1;
   }

   private boolean noPlaceholders() {
      return from == -1 && to == -1;
   }

}
