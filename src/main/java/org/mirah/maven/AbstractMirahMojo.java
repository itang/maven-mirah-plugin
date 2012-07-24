package org.mirah.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.CompilerMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.mirah.MirahCommand;

public abstract class AbstractMirahMojo extends CompilerMojo {
    /**
     * The project itself. This parameter is set by maven.
     * 
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;
    /**
     * Project classpath.
     * 
     * @parameter default-value="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    protected List<String> classpathElements;
    /**
     * The source directories containing the sources to be compiled.
     * 
     * @parameter default-value="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    protected List<String> compileSourceRoots;
    /**
     * Classes destination directory
     * 
     * @parameter expression="${project.build.outputDirectory}"
     */
    protected String outputDirectory;
    /**
     * Classes source directory
     * 
     * @parameter expression="src/main/mirah"
     */
    protected String sourceDirectory;
    /**
     * Show log
     * 
     * @parameter verbose, default false
     */
    protected boolean verbose;

    protected void executeMirahCompiler(String output, boolean bytecode)
            throws MojoExecutionException {
        File d = new File(output);
        if (!d.exists()) {
            d.mkdirs();
        }

        List<String> arguments = new ArrayList<String>();
        if (!bytecode)
            arguments.add("--java");
        if (verbose)
            arguments.add("-V");

        arguments.add("--cd");
        arguments.add(Helper.getFullPath(project, sourceDirectory));
        // arguments.add(sourceDirectory);
        arguments.add("-d");
        arguments.add(Helper.getFullPath(project, output));

        /* do I really need this? */
        arguments.add("-c");
        arguments.add(StringUtils.join(classpathElements.iterator(),
                File.pathSeparator));

        arguments.add(".");

        try {
            MirahCommand.compile(arguments);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
