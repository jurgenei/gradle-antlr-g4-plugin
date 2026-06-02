package name.jurgenei.grammars.g4;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

public class XmlAstG4PluginTest {

    @Test
    public void registersPreconfiguredTaskType() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");

        new XmlAstG4Plugin().apply(project);

        Assert.assertTrue(project.getTasks().getByName("g4XmlAst") instanceof XmlAstG4GradleTask);
    }

    @Test
    public void preconfiguresG4Defaults() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");

        new XmlAstG4Plugin().apply(project);

        final XmlAstG4GradleTask task = (XmlAstG4GradleTask) project.getTasks().getByName("g4XmlAst");
        Assert.assertEquals("antlr4", task.getGrammar().get());
        Assert.assertEquals("name.jurgenei.parsers.ANTLRv4Parser", task.getParserClassName().get());
        Assert.assertEquals("name.jurgenei.parsers.ANTLRv4Lexer", task.getLexerClassName().get());
        Assert.assertEquals("grammarSpec", task.getStartRule().get());
        Assert.assertTrue(task.getIncludes().get().contains("**/*.g4"));
    }
}

