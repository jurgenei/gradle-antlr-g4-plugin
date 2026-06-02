package name.jurgenei.grammars.g4;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Marker Gradle plugin used to validate publication metadata for this module.
 */
public final class G4GrammarPlugin implements Plugin<Project> {

    /**
     * Creates the marker plugin instance.
     */
    public G4GrammarPlugin() {
    }

    @Override
    public void apply(Project project) {
        // Intentionally no-op: this module primarily publishes grammar artifacts.
    }
}

