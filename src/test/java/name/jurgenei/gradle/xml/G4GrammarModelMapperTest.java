package name.jurgenei.gradle.xml;

import name.jurgenei.ast.core.AstClassDeriver;
import name.jurgenei.ast.core.model.AstInheritance;
import name.jurgenei.ast.core.model.AstRelation;
import name.jurgenei.ast.core.model.Cardinality;
import name.jurgenei.ast.core.model.GrammarModel;
import name.jurgenei.ast.core.model.RelationKind;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
    public void writesGrammarModelSexpr() {
        final GrammarModel model = new GrammarModel(java.util.List.of(
                new name.jurgenei.ast.core.model.GrammarRule("assignment",
                        new name.jurgenei.ast.core.model.SequenceNode(java.util.List.of(
                                new name.jurgenei.ast.core.model.LabelNode("target", new name.jurgenei.ast.core.model.RuleRefNode("identifier")))))));

        final String text = new G4GrammarModelSexprWriter().write(model);

        Assert.assertTrue(text.contains("(rule assignment"));
        Assert.assertTrue(text.contains("(label target (ruleRef identifier))"));
    }
}
