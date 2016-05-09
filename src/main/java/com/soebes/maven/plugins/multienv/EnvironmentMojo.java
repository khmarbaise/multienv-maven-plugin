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
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

/**
 * This mojo will get the main artifact of the current project unpack it and use the files of the appropriate
 * environment and produce new files which contain the original files and the supplemental files which have been given
 * by the configuration for each environment.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "environment", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class EnvironmentMojo
    extends AbstractMultiEnvMojo
{

    /**
     * The JAR archiver needed for archiving the environments.
     */
    @Component( role = Archiver.class, hint = "jar" )
    private JarArchiver jarArchiver;

    @Component
    private ArchiverManager manager;

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

        String archiveExt = getArchiveExtensionOfTheProjectMainArtifact();

        File unpackFolder = createUnpackFolder();

        File resourceResult = createPluginResourceOutput();

        filterResources( resourceResult );

        // Currently we use the main artifact of the project
        // TODO: May be should make this configurable? So we might use any kind of artifact
        // as source?
        unarchiveFile( getMavenProject().getArtifact().getFile(), unpackFolder, archiveExt );

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
                File createArchiveFile = createArchiveFile( unpackFolder, targetFolder, environment, archiveExt );
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

    private void createGZIPArchive( String includes )
        throws IOException, NoSuchArchiverException
    {
        try
        {
            File baseFolder = new File( getSourceDirectory(), includes );
            File theOriginalFile = new File( baseFolder, "first.properties" );
            Archiver gzipArchiver = manager.getArchiver( "gzip" );

            gzipArchiver.addFile( theOriginalFile, "first.properties.gz" );

            gzipArchiver.setDestFile( new File( baseFolder, "first.properties.gz" ) );
            gzipArchiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            getLog().error( "Archive creation failed.", e );
        }

    }

    private void unarchiveFile( File sourceFile, File destDirectory, String archiveExt )
        throws MojoExecutionException
    {
        try
        {
            UnArchiver unArchiver = manager.getUnArchiver( archiveExt );

            unArchiver.setSourceFile( sourceFile );
            unArchiver.setUseJvmChmod( true );
            unArchiver.setDestDirectory( destDirectory );
            unArchiver.setOverwrite( true );
            unArchiver.extract();
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Error unpacking file [" + sourceFile.getAbsolutePath() + "]" + " to ["
                + destDirectory.getAbsolutePath() + "]", e );
        }
        catch ( NoSuchArchiverException e )
        {
            getLog().error( "Unknown archiver." + " with unknown extension [" + archiveExt + "]" );
        }
    }

    private File createArchiveFile( File unpackFolder, File targetFolder, String folder, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        final MavenArchiver mavenArchiver = new MavenArchiver();

        mavenArchiver.setArchiver( jarArchiver );

        jarArchiver.addFileSet( new DefaultFileSet( targetFolder ) );
        jarArchiver.addFileSet( new DefaultFileSet( unpackFolder ) );
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
