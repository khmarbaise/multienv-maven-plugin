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
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.util.StringUtils;

/**
 * This goal will create separate packages out of the given environment directory.
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

    /**
     * The kind of archive we should produce {@code zip}, {code jar} etc.
     */
    @Parameter( defaultValue = "jar" )
    private String archiveType;

    @Override
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

        File resourceResult = createPluginResourceOutput();


        for ( String environment : identifiedEnvironments )
        {
            getLog().info( "Building Environment: '" + environment + "'" );
            // Check why this can happen?
            if ( environment.isEmpty() )
            {
                getLog().warn( "The given directory '" + environment + "' is empty." );
                continue;
            }
            
            if (shouldSkip(environment)) {
                continue;
            }
            
            filterResources( resourceResult, environment );
            
            File commonDirectory = null;
            if (StringUtils.isNotBlank(getCommonDir())) {
                commonDirectory = new File(resourceResult, getCommonDir());
            }
            
            try
            {
                File targetDirectory = new File( resourceResult, environment );
                File createArchiveFile = createArchiveFile( targetDirectory, commonDirectory, environment, archiveType );
                getProjectHelper().attachArtifact( getMavenProject(), archiveType, environment,
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

    private File createArchiveFile( File targetDirectory, File commonDirectory, String directory, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        final MavenArchiver mavenArchiver = new MavenArchiver();

        mavenArchiver.setArchiver( jarArchiver );

        jarArchiver.addFileSet( new DefaultFileSet( targetDirectory ) );
        if (commonDirectory != null) {
            jarArchiver.addFileSet( new DefaultFileSet( commonDirectory ) );
        }
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
