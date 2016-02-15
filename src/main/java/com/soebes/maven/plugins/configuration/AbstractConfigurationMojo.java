package com.soebes.maven.plugins.configuration;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

public abstract class AbstractConfigurationMojo
    extends AbstractMojo
{

    /**
     * The project currently being build.
     */
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    /**
     * The current Maven session.
     */
    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    /**
     * The directory for the generated WAR.
     */
    @Parameter( defaultValue = "${project.build.directory}", required = true )
    private String outputDirectory;

    @Parameter( defaultValue = "${project.build.outputDirectory}", readonly = true )
    private File environment;

    /**
     * folder which contains the different environments
     */
    @Parameter( defaultValue = "${basedir}/src/main/environments" )
    private File sourceDirectory;

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter( defaultValue = "${project.build.sourceEncoding}" )
    private String encoding;

    @Component
    private MavenProjectHelper projectHelper;

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public MavenProjectHelper getProjectHelper()
    {
        return projectHelper;
    }

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

}
