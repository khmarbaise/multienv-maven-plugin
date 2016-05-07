package com.soebes.maven.plugins.multienv;

import java.io.File;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.DirectoryScanner;

public abstract class AbstractMultiEnvMojo
    extends AbstractMojo
{

    /**
     * The project currently being build.
     */
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject mavenProject;

    /**
     * The current Maven session.
     */
    @Parameter( defaultValue = "${session}", required = true, readonly = true )
    private MavenSession mavenSession;

    /**
     * The directory for the generated configuration packages.
     */
    @Parameter( defaultValue = "${project.build.directory}", required = true, readonly = true )
    private File outputDirectory;

    /**
     * folder which contains the different environments
     */
    // TODO: src/main ? property?
    @Parameter( defaultValue = "${basedir}/src/main/environments" )
    private File sourceDirectory;

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter( defaultValue = "${project.build.sourceEncoding}" )
    private String encoding;

    /**
     * Name of the generated JAR.
     */
    @Parameter( defaultValue = "${project.build.finalName}", readonly = true )
    private String finalName;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
     * Archiver Reference</a>.
     */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    public MavenArchiveConfiguration getArchive()
    {
        return archive;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public File getOutputDirectory()
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

    public String getFinalName()
    {
        return finalName;
    }

    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param resourceResult The folder where to search for different environments.
     * @return The list of identified environments.
     */
    protected String[] getTheEnvironments( File resourceResult )
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( resourceResult );
        // It is necessary to exclude the {@code ""} cause
        // otherwise we would get back this as well.
        // Bug?
        ds.setExcludes( new String[] { "" } );
        ds.addDefaultExcludes();
        ds.scan();

        return ds.getIncludedDirectories();
    }

    /**
     * Returns the archive file to generate, based on an optional classifier.
     *
     * @param basedir the output directory
     * @param finalName the name of the ear file
     * @param classifier an optional classifier
     * @return the file to generate
     */
    protected File getArchiveFile( File basedir, String finalName, String classifier, String archiveExt )
    {
        if ( basedir == null )
        {
            throw new IllegalArgumentException( "basedir is not allowed to be null" );
        }
        if ( finalName == null )
        {
            throw new IllegalArgumentException( "finalName is not allowed to be null" );
        }
        if ( archiveExt == null )
        {
            throw new IllegalArgumentException( "archiveExt is not allowed to be null" );
        }

        if ( finalName.isEmpty() )
        {
            throw new IllegalArgumentException( "finalName is not allowed to be empty." );
        }
        if ( archiveExt.isEmpty() )
        {
            throw new IllegalArgumentException( "archiveExt is not allowed to be empty." );
        }

        StringBuilder fileName = new StringBuilder( finalName );

        if ( hasClassifier( classifier ) )
        {
            fileName.append( "-" ).append( classifier );
        }

        fileName.append( '.' );
        fileName.append( archiveExt );

        return new File( basedir, fileName.toString() );
    }

    private boolean hasClassifier( String classifier )
    {
        boolean result = false;
        if ( classifier != null && classifier.trim().length() > 0 )
        {
            result = true;
        }

        return result;
    }

}
