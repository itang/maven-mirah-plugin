package org.mirah.maven;

import org.apache.maven.plugin.CompilationFailureException;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Test Compiles Mirah source files
 * 
 * @goal test-compile
 * @phase test-compile
 * @threadSafe
 * @requiresDependencyResolution test
 */
public class MirahTestCompilerMojo extends AbstractTestMirahMojo {
    public void execute() throws MojoExecutionException,
            CompilationFailureException {
        executeMirahCompiler(testOutputDirectory, true);
    }
}