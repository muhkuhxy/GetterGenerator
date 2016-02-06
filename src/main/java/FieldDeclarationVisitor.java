import java.util.ArrayList;
import java.util.List;

final class FieldDeclarationVisitor extends JavaBaseListener {
   private boolean outerMostType = true;
   private String type;
   List<String> names = new ArrayList<>();

   @Override
   public void enterType(final JavaParser.TypeContext ctx) {
      if(outerMostType) {
         type = ctx.getText();
         outerMostType = false;
      }
   }

   @Override
   public void enterVariableDeclaratorId(final JavaParser.VariableDeclaratorIdContext ctx) {
      names.add(ctx.getText());
   }

   public String getType() {
      return type;
   }

   public List<String> getNames() {
      return names;
   }
}