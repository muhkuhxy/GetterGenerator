import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.xpath.XPath;

public class GetterAnalyzer extends JavaBaseListener {

	private final Stack<String> className = new Stack<>();
	private JavaParser parser;
	private final List<GetterSpec> getters = new ArrayList<>();

	public GetterAnalyzer(final String file) {
		try {
			final ANTLRFileStream input = new ANTLRFileStream(file);
			final JavaLexer lexer = new JavaLexer(input);
			final CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new JavaParser(tokens);
			System.out.println();
		} catch (final IOException e) {
			throw new IllegalArgumentException("file not found", e);
		}
	}

	public List<GetterSpec> analyze() {
		final JavaParser.CompilationUnitContext tree = parser.compilationUnit();
		ParseTreeWalker.DEFAULT.walk(this, tree);
		return getters;
	}

	@Override
	public void enterClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {
		final String className = ctx.getChild(1).getText();
		this.className.push(className);
	}

	@Override
	public void enterClassBodyDeclaration(final JavaParser.ClassBodyDeclarationContext ctx) {
		final boolean optional = hasNullableAnnotation(parser, ctx.getPayload());
		for (final ParseTree field : XPath.findAll(ctx.getPayload(), "*/memberDeclaration/fieldDeclaration", parser)) {
			final FieldDeclarationVisitor fieldVisitor = new FieldDeclarationVisitor();
			ParseTreeWalker.DEFAULT.walk(fieldVisitor, field);
			fieldVisitor.getNames().forEach(n -> {
				getters.add(new GetterSpec(fieldVisitor.getType(), n, className.peek(), optional));
			});
		}
	}

	@Override
	public void exitClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {
		className.pop();
	}

	private boolean hasNullableAnnotation(final JavaParser parser, final ParseTree declaration) {
		final ParseTreePattern p = parser.compileParseTreePattern("@ Nullable", JavaParser.RULE_annotation);
		final boolean nullable = !p.findAll(declaration, "*/modifier/classOrInterfaceModifier/annotation").isEmpty();
		return nullable;
	}
}
