package name.jurgenei.gradle.antlr;

import name.jurgenei.gradle.xml.G4toClassTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Registers G4-specific tasks for ANTLRv4 grammar processing.
 */
public final class XmlAstG4Plugin implements Plugin<Project> {

    /**
     * Creates the G4 XML AST plugin.
     */
    public XmlAstG4Plugin() {
    }

    @Override
    public void apply(final Project project) {
        LanguagePluginSupport.registerXmlAstTask(
                project,
                "g4XmlAst",
                XmlAstG4GradleTask.class,
                "Convert ANTLRv4 grammar files to XML AST output.");
        LanguagePluginSupport.wireJavaRuntimeClasspath(project, XmlAstG4GradleTask.class);

        project.getTasks().register("g4ToClass", G4toClassTask.class, task -> {
            task.setGroup("xmlast");
            task.setDescription("Convert ANTLRv4 grammar files to GrammarModel and AST-Classes output.");
        });
    }
}
