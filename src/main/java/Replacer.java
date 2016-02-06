import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

public class Replacer {

   private final String placeholder;

   public Replacer(final String placeholder) {
      this.placeholder = placeholder;
   }

   public void replaceIn(final String fileName, final String getters) {
      try {
         final File file = new File(fileName);
         final List<String> lines = Files.readLines(file, Charsets.UTF_8);
         int from = -1, to = -1;
         for(int i = lines.size()-1; i >= 0; i--) {
            if(lines.get(i).contains(placeholder)) {
               if(to==-1) {
                  to = i;
                  continue;
               } else if(from == -1) {
                  from = i+1;
                  break;
               }
            }
            if(to != -1) {
               lines.remove(i);
            }
         }
         if(from == -1 || to == -1) {
            throw new IllegalStateException("mark area in file with "+placeholder+" on different lines");
         }
         lines.add(from,getters);
         Files.write(Joiner.on("\n").join(lines), file, Charsets.UTF_8);
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
