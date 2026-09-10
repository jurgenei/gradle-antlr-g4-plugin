package name.jurgenei.gradle.antlr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Registers a G4-specific XML AST task with parser defaults for ANTLRv4 grammars.
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
    }
}

