package com.soebes.maven.plugins.multienv;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
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
 * environment and produce new files which contain the original files plus the supplemental files which have been given
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
            getLog().warn( "No Environment directories found." );
            return;
        }

        validateEnvironments( identifiedEnvironments );

        createLoggingOutput( identifiedEnvironments );

        Artifact artifact = getMavenSession().getCurrentProject().getArtifact();
        String archiveExt = "zip";
        if ( artifact.getFile().isFile() )
        {
            archiveExt = getArchiveExtensionOfTheArtifact( artifact );
            getLog().info( "Selected main artifact " + artifact.getId() + " of the project for further processing." );
        }
        else
        {
            List<Artifact> attachedArtifacts = getMavenSession().getCurrentProject().getAttachedArtifacts();
            if ( attachedArtifacts.size() > 1 )
            {
                getLog().error( "We can not decide which attached artifact to use." );
                throw new MojoExecutionException( "We can not decide which attached artifact to be used." );
            }

            archiveExt = getArchiveExtensionOfTheArtifact( attachedArtifacts.get( 0 ) );
            artifact = attachedArtifacts.get( 0 );
            getLog().info( "Selected attached artifact " + artifact.getId() + " of the project for further processing." );
        }

        File unpackDirectory = createUnpackDirectory();

        File resourceResult = createPluginResourceOutput();

        filterResources( resourceResult );

        unarchiveFile( artifact.getFile(), unpackDirectory, archiveExt );

        for ( String environment : identifiedEnvironments )
        {
            getLog().info( "Building Environment: '" + environment + "'" );

            // Check why this can happen?
            if ( environment.isEmpty() )
            {
                getLog().warn( "The given directory '" + environment + "' is empty." );
                continue;
            }

            try
            {
                File targetDirectory = new File( resourceResult, environment );
                File createArchiveFile = createArchiveFile( unpackDirectory, targetDirectory, environment, archiveExt );
                getProjectHelper().attachArtifact( getMavenProject(), getMavenProject().getPackaging(), environment,
                                                   createArchiveFile );
            }
            catch ( NoSuchArchiverException e )
            {
                getLog().error( "Archive creation failed.", e );
                throw new MojoExecutionException( "Archive creation failed.", e );
            }
            catch ( IOException e )
            {
                getLog().error( "IO Exception.", e );
                throw new MojoExecutionException( "IO Exception.", e );
            }
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

    private File createArchiveFile( File unpackDirectory, File targetDirectory, String directory, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        final MavenArchiver mavenArchiver = new MavenArchiver();

        mavenArchiver.setArchiver( jarArchiver );

        jarArchiver.addFileSet( new DefaultFileSet( targetDirectory ) );
        jarArchiver.addFileSet( new DefaultFileSet( unpackDirectory ) );
        // jarArchiver.setDuplicateBehavior( duplicate );

        File resultArchive = getArchiveFile( getOutputDirectory(), getFinalName(), directory, archiveExt );

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
