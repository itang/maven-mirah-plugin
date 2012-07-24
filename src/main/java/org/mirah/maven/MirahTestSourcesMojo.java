package org.mirah.maven;

import org.apache.maven.plugin.CompilationFailureException;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Test Compiles Mirah source files
 * 
 * @goal generate-test-sources
 * @phase generate-test-sources
 * @threadSafe
 * @requiresDependencyResolution test
 */
public class MirahTestSourcesMojo extends AbstractTestMirahMojo {
    public void execute() throws MojoExecutionException,
            CompilationFailureException {
        String javaSourceRoot = testCompileSourceRoots.get(0);
        executeMirahCompiler(javaSourceRoot, false);
    }
}
