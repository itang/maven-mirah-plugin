package org.mirah.maven;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.CompilerMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.mirah.MirahCommand;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class AbstractTestMirahMojo extends CompilerMojo {

    protected static final Predicate<String> FILE_EXISTS = new Predicate<String>() {

        public boolean apply(String filePath) {
            return new File(filePath).exists();
        }
    };
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
    protected List<String> testClasspathElements;
    /**
     * The source directories containing the sources to be compiled.
     * 
     * @parameter default-value="${project.testCompileSourceRoots}"
     * @required
     * @readonly
     */
    protected List<String> testCompileSourceRoots;
    /**
     * Test Classes destination directory
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     */
    protected String testOutputDirectory;
    /**
     * Test Classes source directory
     * 
     * @parameter expression="src/test/mirah"
     */
    protected String testSourceDirectory;

    /**
     * Show log
     * 
     * @parameter verbose, default false
     */
    protected boolean verbose;

    protected void executeMirahCompiler(String output, boolean bytecode)
            throws MojoExecutionException {
        DEBUG("output", output);
        DEBUG("testOutputDirectory", testOutputDirectory);

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

        DEBUG("testSourceDirectory", testSourceDirectory);
        // why should use absolute path?
        arguments.add(Helper.getFullPath(project, testSourceDirectory));

        arguments.add("-d");
        arguments.add(Helper.getFullPath(project, output));

        String testClassPath = StringUtils.join(getTestClassPath().iterator(),
                File.pathSeparator);

        DEBUG("classpath: ", testClassPath);

        /* do I really need this? */
        arguments.add("-c");
        arguments.add(testClassPath);

        arguments.add(".");

        DEBUG(StringUtils.join(arguments.iterator(), " "));

        try {
            MirahCommand.compile(arguments);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void DEBUG(String... args) {
        StringBuilder sb = new StringBuilder("***DEBUG:[");
        for (String s : args) {
            sb.append(s).append(" ");
        }
        sb.append("]");
        System.out.println(sb.toString());
    }

    @SuppressWarnings("unchecked")
    protected List<String> getTestClassPath() {
        Set<String> classPath = Sets.newLinkedHashSet();
        classPath.add(Helper.getFullPath(project, testSourceDirectory));
        try {
            classPath.addAll(project.getTestClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException("getTestClassPath", e);
        }
        addDependencies(classPath, project.getTestArtifacts());
        return newArrayList(filter(classPath, FILE_EXISTS));
    }

    private void addDependencies(Set<String> classPath,
            List<Artifact> dependencies) {
        for (Artifact artifact : dependencies) {
            classPath.add(artifact.getFile().getAbsolutePath());
        }
    }
}
