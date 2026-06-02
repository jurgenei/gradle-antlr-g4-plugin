package name.jurgenei.grammars.g4;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Legacy compatibility alias for the G4 XML AST plugin.
 *
 * <p>New builds should use {@link XmlAstG4Plugin} (and plugin id
 * {@code name.jurgenei.gradle.antlr.g4}).</p>
 */
@Deprecated(forRemoval = false)
public final class G4GrammarPlugin implements Plugin<Project> {

    /**
     * Creates the legacy compatibility plugin adapter.
     */
    public G4GrammarPlugin() {
    }

    @Override
    public void apply(final Project project) {
        new XmlAstG4Plugin().apply(project);
    }
}

