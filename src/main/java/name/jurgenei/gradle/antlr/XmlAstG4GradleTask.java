package name.jurgenei.gradle.antlr;

import name.jurgenei.gradle.antlr.XmlAstGradleTask;
import org.gradle.work.DisableCachingByDefault;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * G4-flavored {@link XmlAstGradleTask} with ANTLRv4 parser defaults.
 */
@DisableCachingByDefault(because = "XmlAstGradleTask performs external parser loading and file-system driven conversion not yet declared for safe caching")
public abstract class XmlAstG4GradleTask extends XmlAstGradleTask {

    /**
     * Creates a preconfigured G4 XML AST task.
     *
     * @param objects Gradle object factory.
     */
    @Inject
    public XmlAstG4GradleTask(final ObjectFactory objects) {
        super(objects);
        getGrammar().convention("antlr4");
        getParserClassName().convention("name.jurgenei.parsers.ANTLRv4Parser");
        getLexerClassName().convention("name.jurgenei.parsers.ANTLRv4Lexer");
        getStartRule().convention("grammarSpec");
        getIncludes().convention(List.of("**/*.g4"));
    }
}

