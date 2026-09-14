package name.jurgenei.gradle.xml;

import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class G4toClassTaskTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void explicitModeCreatesClassAndModelFiles() throws Exception {
        final File projectDir = temporaryFolder.newFolder("unit-explicit-mode");
        final Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        final G4toClassTask task = project.getTasks().create("g4toClassUnit", G4toClassTask.class);

        final File input = writeGrammar(projectDir, "Mini.g4", sampleGrammar());
        task.input(input);
        task.output(new File(projectDir, "build/out/Mini.classes.sexp"));

        task.convertAll();

        Assert.assertTrue(new File(projectDir, "build/out/Mini.classes.sexp").isFile());
        Assert.assertTrue(new File(projectDir, "build/out/Mini.classes.model.sexp").isFile());

        final String classes = Files.readString(new File(projectDir, "build/out/Mini.classes.sexp").toPath(), StandardCharsets.UTF_8);
        Assert.assertTrue(classes.contains("(class Assignment)"));
    }

    @Test
    public void fileTreeModeCreatesOutputsForAllInputs() throws Exception {
        final File projectDir = temporaryFolder.newFolder("unit-fileset-mode");
        final Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        final G4toClassTask task = project.getTasks().create("g4toClassUnit", G4toClassTask.class);

        writeGrammar(projectDir, "src/main/antlr/a/One.g4", sampleGrammar());
        writeGrammar(projectDir, "src/main/antlr/b/Two.g4", sampleGrammar());

        task.fileset(new File(projectDir, "src/main/antlr"), (ConfigurableFileTree tree) -> tree.include("**/*.g4"));
        task.getOutputDir().set(new File(projectDir, "build/derived"));

        task.convertAll();

        Assert.assertTrue(new File(projectDir, "build/derived/a/One.classes.sexp").isFile());
        Assert.assertTrue(new File(projectDir, "build/derived/a/One.model.sexp").isFile());
        Assert.assertTrue(new File(projectDir, "build/derived/b/Two.classes.sexp").isFile());
        Assert.assertTrue(new File(projectDir, "build/derived/b/Two.model.sexp").isFile());
    }

    @Test
    public void supportsCustomModelOutputPath() throws Exception {
        final File projectDir = temporaryFolder.newFolder("unit-custom-model-output");
        final Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        final G4toClassTask task = project.getTasks().create("g4toClassUnit", G4toClassTask.class);

        final File input = writeGrammar(projectDir, "Mini.g4", sampleGrammar());
        task.input(input);
        task.output(new File(projectDir, "build/out/Mini.classes.sexp"));
        task.modelOutput(new File(projectDir, "build/model/Mini-custom.model.sexp"));

        task.convertAll();

        Assert.assertTrue(new File(projectDir, "build/model/Mini-custom.model.sexp").isFile());
    }

    @Test(expected = org.gradle.api.GradleException.class)
    public void failsWhenOutputDirMissingInFileTreeMode() throws Exception {
        final File projectDir = temporaryFolder.newFolder("unit-missing-output-dir");
        final Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        final G4toClassTask task = project.getTasks().create("g4toClassUnit", G4toClassTask.class);

        writeGrammar(projectDir, "src/main/antlr/Mini.g4", sampleGrammar());
        task.fileset(new File(projectDir, "src/main/antlr"), (ConfigurableFileTree tree) -> tree.include("**/*.g4"));

        task.convertAll();
    }

    @Test
    public void doesNotThrowWhenFailOnErrorFalse() throws Exception {
        final File projectDir = temporaryFolder.newFolder("unit-fail-on-error-false");
        final Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        final G4toClassTask task = project.getTasks().create("g4toClassUnit", G4toClassTask.class);

        task.getFailOnError().set(false);
        task.input(new File(projectDir, "missing.g4"));
        task.output(new File(projectDir, "build/out/missing.classes.sexp"));

        task.convertAll();

        Assert.assertFalse(new File(projectDir, "build/out/missing.classes.sexp").exists());
    }

    private File writeGrammar(final File projectDir, final String relativePath, final String grammar) throws Exception {
        final File file = new File(projectDir, relativePath);
        final File parent = file.getParentFile();
        if (parent != null) {
            Assert.assertTrue(parent.mkdirs() || parent.isDirectory());
        }
        Files.writeString(file.toPath(), grammar, StandardCharsets.UTF_8);
        return file;
    }

    private String sampleGrammar() {
        return """
                grammar Mini;

                assignment
                  : target=identifier '=' value=expression ';'
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
    }
}
