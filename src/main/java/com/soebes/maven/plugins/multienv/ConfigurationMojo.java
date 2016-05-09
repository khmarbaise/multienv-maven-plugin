package com.soebes.maven.plugins.multienv;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

/**
 * This goal will create separate packages out of the given environment folder.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "configuration", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class ConfigurationMojo
    extends AbstractMultiEnvMojo
{

    /**
     * The JAR archiver needed for archiving the environments.
     */
    @Component( role = Archiver.class, hint = "jar" )
    private JarArchiver jarArchiver;

    @Component
    private ArchiverManager manager;

    @Component( role = MavenFileFilter.class, hint = "default" )
    private MavenFileFilter mavenFileFilter;

    /**
     * The kind of archive we should produce {@code zip}, {code jar} etc.
     */
    @Parameter( defaultValue = "jar" )
    private String archiveType;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String[] identifiedEnvironments = getTheEnvironments( getSourceDirectory() );

        if ( identifiedEnvironments.length == 0 )
        {
            getLog().warn( "No Environment folders found." );
            return;
        }

        createLoggingOutput( identifiedEnvironments );

        File resourceResult = createPluginResourceOutput();

        filterResources( resourceResult );

        for ( String environment : identifiedEnvironments )
        {
            getLog().info( "Building Environment: '" + environment + "'" );

            // Check why this can happen?
            if ( environment.isEmpty() )
            {
                getLog().warn( "The given folder '" + environment + "' is empty." );
                continue;
            }

            try
            {
                File targetFolder = new File( resourceResult, environment );
                File createArchiveFile = createArchiveFile( targetFolder, environment, archiveType );
                getProjectHelper().attachArtifact( getMavenProject(), getMavenProject().getPackaging(), environment,
                                                   createArchiveFile );
            }
            catch ( NoSuchArchiverException e )
            {
                getLog().error( "Archive creation failed.", e );
            }
            catch ( IOException e )
            {
                getLog().error( "IO Exception.", e );
            }
        }

    }

    private File createArchiveFile( File targetFolder, String folder, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        final MavenArchiver mavenArchiver = new MavenArchiver();

        mavenArchiver.setArchiver( jarArchiver );

        jarArchiver.addFileSet( new DefaultFileSet( targetFolder ) );
        // jarArchiver.setDuplicateBehavior( duplicate );

        File resultArchive = getArchiveFile( getOutputDirectory(), getFinalName(), folder, archiveExt );

        mavenArchiver.setOutputFile( resultArchive );
        try
        {
            mavenArchiver.createArchive( getMavenSession(), getMavenProject(), getArchive() );
        }
        catch ( ArchiverException | ManifestException | DependencyResolutionRequiredException e )
        {
            getLog().error( e.getMessage(), e );
            throw new MojoExecutionException( e.getMessage(), e );
        }

        return resultArchive;

    }

}
