package org.mirah.maven;

import java.io.File;

import org.apache.maven.project.MavenProject;

public final class Helper {
    public static String getFullPath(MavenProject project, String file) {
        if (file.startsWith("src/") || file.startsWith("target/")) {
            return new File(project.getBasedir(), file).getAbsolutePath();
        }
        return file;
    }
}
