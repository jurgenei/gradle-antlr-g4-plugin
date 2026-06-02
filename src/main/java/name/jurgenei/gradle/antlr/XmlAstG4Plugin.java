package name.jurgenei.gradle.antlr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

/**
 * Registers a G4-specific XML AST task with parser defaults for ANTLRv4 grammars.
 */
public final class XmlAstG4Plugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getTasks().register("g4XmlAst", XmlAstG4GradleTask.class, task -> {
            task.setGroup("xmlast");
            task.setDescription("Convert ANTLRv4 grammar files to XML AST output.");
        });

        project.getPlugins().withId("java", plugin -> {
            final JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
            final SourceSetContainer sourceSets = javaPluginExtension.getSourceSets();
            final SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);

            project.getTasks().withType(XmlAstG4GradleTask.class).configureEach(task -> {
                task.getRuntimeClasspath().from(mainSourceSet.getRuntimeClasspath());
                task.dependsOn(project.getTasks().named("classes"));
            });
        });
    }
}

