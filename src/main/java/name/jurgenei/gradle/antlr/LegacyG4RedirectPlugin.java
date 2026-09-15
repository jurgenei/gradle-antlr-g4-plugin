package name.jurgenei.gradle.antlr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Legacy bridge plugin. Keeps old plugin id alive while routing behavior to merged core plugin.
 */
@SuppressWarnings("unused")
public final class LegacyG4RedirectPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getLogger().warn("name.jurgenei.gradle.antlr.g4 is deprecated; migrate to name.jurgenei.gradle.antlr (g4 tasks included)." );
        project.getPluginManager().apply(CompatG4Plugin.class);
    }
}

