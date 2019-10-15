package com.soebes.maven.plugins.multienv;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

/**
 * This goal will create separate packages out of the given environment directory.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "configuration", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class ConfigurationMojo
    extends AbstractMultiEnvMojo
{
    private static final String EXTENSION_TAR = "tar";
    
    @Component
    private ArchiverManager manager;

    /**
     * The kind of archive we should produce {@code zip}, {@code jar} etc.
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

        filterResources( resourceResult );

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
                File createArchiveFile = createArchiveFile( targetDirectory, environment, archiveType );
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

    private File createArchiveFile( File targetDirectory, String directory, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        File resultArchive = getArchiveFile( getOutputDirectory(), getFinalName(), directory, archiveExt );
        
        Archiver archiver;
        if ( archiveExt.startsWith(EXTENSION_TAR) )
        {
            archiver = createTarArchiver( archiveExt );
        }
        else
        {
            archiver = manager.getArchiver( resultArchive ); 
        }
        
        archiver.addFileSet( new DefaultFileSet( targetDirectory ) );
        
        archiver.setDestFile(resultArchive);
        
        try
        {
            archiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            getLog().error( e.getMessage(), e );
            throw new MojoExecutionException( e.getMessage(), e );
        }

        return resultArchive;

    }
    
    /**
     * Create an archiver for tar(.*) formats
     * @param archiveExt extension of the archive
     * @return the appropriate Archiver
     * @throws NoSuchArchiverException
     */
    private Archiver createTarArchiver( String archiveExt )
        throws NoSuchArchiverException
    {
        TarArchiver tarArchiver = (TarArchiver) manager.getArchiver(EXTENSION_TAR);
        int index = archiveExt.indexOf( '.' );
        if ( index >= 0 )
        {
            TarArchiver.TarCompressionMethod compressionMethod;
            
            String compression = archiveExt.substring( index + 1 );
            switch (compression) {
                case "gz":
                    compressionMethod = TarArchiver.TarCompressionMethod.gzip;
                    break;
                case "bz2":
                    compressionMethod = TarArchiver.TarCompressionMethod.bzip2;
                    break;
                default:
                    compressionMethod = TarArchiver.TarCompressionMethod.valueOf(compression);
                    break;
            }
            
            if (compressionMethod == null) {
                throw new IllegalArgumentException( "Unknown compression format: " + compression );
            }
            
            tarArchiver.setCompression( compressionMethod );
        }

        return tarArchiver;
    }
}
