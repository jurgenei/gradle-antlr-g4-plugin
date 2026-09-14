package name.jurgenei.gradle.xml;

import name.jurgenei.ast.core.AstClassDeriver;
import name.jurgenei.ast.core.model.AstInheritance;
import name.jurgenei.ast.core.model.AstRelation;
import name.jurgenei.ast.core.model.Cardinality;
import name.jurgenei.ast.core.model.ChoiceNode;
import name.jurgenei.ast.core.model.GrammarModel;
import name.jurgenei.ast.core.model.GrammarRule;
import name.jurgenei.ast.core.model.LabelNode;
import name.jurgenei.ast.core.model.LiteralNode;
import name.jurgenei.ast.core.model.OptionalNode;
import name.jurgenei.ast.core.model.RelationKind;
import name.jurgenei.ast.core.model.Repeat1Node;
import name.jurgenei.ast.core.model.RepeatNode;
import name.jurgenei.ast.core.model.RuleRefNode;
import name.jurgenei.ast.core.model.SequenceNode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class G4GrammarModelMapperTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void mapsRuleSemanticsFor1B2A3A() throws Exception {
        final File grammarFile = temporaryFolder.newFile("mini.g4");
        final String grammar = """
                grammar Mini;

                assignment
                  : target=identifier '=' value=expression ';'
                  ;

                expression
                  : functionCall
                  | binaryExpression
                  | literalExpr
                  ;

                functionCall : identifier ;
                binaryExpression : identifier ;
                literalExpr : INT ;
                identifier : nameToken ;
                nameToken : ID ;
                terminator : ';' ;

                ID : [a-zA-Z_][a-zA-Z0-9_]* ;
                INT : [0-9]+ ;
                WS : [ \t\r\n]+ -> skip ;
                """;
        Files.writeString(grammarFile.toPath(), grammar, StandardCharsets.UTF_8);

        final GrammarModel model = new G4GrammarModelMapper().map(
                grammarFile,
                "name.jurgenei.parsers.ANTLRv4Lexer",
                "name.jurgenei.parsers.ANTLRv4Parser",
                "grammarSpec",
                getClass().getClassLoader());

        final var ast = new AstClassDeriver().derive(model);

        Assert.assertTrue(ast.relations().contains(new AstRelation("Assignment", "target", "Identifier", Cardinality.ONE, RelationKind.REL)));
        Assert.assertTrue(ast.relations().contains(new AstRelation("Assignment", "value", "Expression", Cardinality.ONE, RelationKind.REL)));
        Assert.assertTrue(ast.inheritances().contains(new AstInheritance("FunctionCall", "Expression")));
        Assert.assertTrue(ast.inheritances().contains(new AstInheritance("BinaryExpression", "Expression")));
        Assert.assertFalse(ast.classes().stream().anyMatch(c -> c.name().equals("Terminator")));
    }

    @Test
    public void fallsBackToTextParserWhenRuntimeClassesMissing() throws Exception {
        final File grammarFile = temporaryFolder.newFile("text-fallback.g4");
        final String grammar = """
                grammar Mini;

                procedure
                  : param=identifier* alt=identifier? plusOne=identifier+
                  ;

                expression
                  : functionCall
                  | binaryExpression
                  ;

                functionCall : identifier ;
                binaryExpression : identifier ;
                identifier : nameToken ;
                nameToken : ID ;

                ID : [a-zA-Z_][a-zA-Z0-9_]* ;
                WS : [ \t\r\n]+ -> skip ;
                """;
        Files.writeString(grammarFile.toPath(), grammar, StandardCharsets.UTF_8);

        final GrammarModel model = new G4GrammarModelMapper().map(
                grammarFile,
                "missing.Lexer",
                "missing.Parser",
                "grammarSpec",
                getClass().getClassLoader());

        final String modelText = new G4GrammarModelSexprWriter().write(model);
        Assert.assertTrue(modelText.contains("(rule procedure"));

        final var ast = new AstClassDeriver().derive(model);
        Assert.assertTrue(ast.relations().contains(new AstRelation("Procedure", "param", "Identifier", Cardinality.STAR, RelationKind.REL)));
        Assert.assertTrue(ast.relations().contains(new AstRelation("Procedure", "plusOne", "Identifier", Cardinality.PLUS, RelationKind.REL)));
        Assert.assertTrue(ast.relations().contains(new AstRelation("Procedure", "alt", "Identifier", Cardinality.OPTIONAL, RelationKind.REL)));
    }

    @Test
    public void writesGrammarModelSexprForAllNodeKinds() {
        final GrammarModel model = new GrammarModel(List.of(
                new GrammarRule("assignment",
                        new SequenceNode(List.of(
                                new LabelNode("target", new RuleRefNode("identifier")),
                                new OptionalNode(new LabelNode("maybe", new RuleRefNode("expression"))),
                                new RepeatNode(new LabelNode("items", new RuleRefNode("identifier"))),
                                new Repeat1Node(new LabelNode("onePlus", new RuleRefNode("identifier"))),
                                new ChoiceNode(List.of(new LiteralNode("'x'"), new RuleRefNode("expression"))),
                                new LiteralNode("';'"))))));

        final String text = new G4GrammarModelSexprWriter().write(model);

        Assert.assertTrue(text.contains("(rule assignment"));
        Assert.assertTrue(text.contains("(label target (ruleRef identifier))"));
        Assert.assertTrue(text.contains("(optional (label maybe (ruleRef expression)))"));
        Assert.assertTrue(text.contains("(repeat (label items (ruleRef identifier)))"));
        Assert.assertTrue(text.contains("(repeat1 (label onePlus (ruleRef identifier)))"));
        Assert.assertTrue(text.contains("(choice (literal \"'x'\") (ruleRef expression))"));
    }
}
