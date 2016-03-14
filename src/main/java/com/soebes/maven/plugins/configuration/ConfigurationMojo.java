package com.soebes.maven.plugins.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Will check the parent of your project and fail the build if the parent is not the newest available version of the
 * parent.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "configuration", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class ConfigurationMojo
    extends AbstractConfigurationMojo
{

    @Component
    private ArchiverManager manager;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( getSourceDirectory() );
        ds.setExcludes( new String[] { ".", "" } );
        ds.addDefaultExcludes();

        ds.scan();

        String[] includedDirectories = ds.getIncludedDirectories();
        for ( String folder : includedDirectories )
        {
            getLog().info( "Environment Folder: '" + folder + "'" );

            // FIXME: Why do we get "" ?
            if ( !folder.isEmpty() )
            {

                try
                {
                    createArchiveFile( folder );
                    createGZIPArchive( folder );
                }
                catch ( NoSuchArchiverException e )
                {
                    e.printStackTrace();
                }
                catch ( IOException e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private void createGZIPArchive( String includes )
        throws IOException, NoSuchArchiverException
    {
        try
        {
            File baseFolder = new File (getSourceDirectory(), includes);
            File theOriginalFile = new File( baseFolder, "first.properties" );
            Archiver gzipArchiver = manager.getArchiver( "gzip" );
            
            gzipArchiver.addFile( theOriginalFile, "first.properties.gz" );

            gzipArchiver.setDestFile( new File(baseFolder, "first.properties.gz") );
            gzipArchiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void createArchiveFile( String includes )
        throws NoSuchArchiverException, IOException
    {
        try
        {
            Archiver zipArchiver = manager.getArchiver( "zip" );

            zipArchiver.addDirectory( new File( getSourceDirectory(), includes ) );

            File zipFile = new File( getOutputDirectory(), includes + "-result.zip" );
            zipArchiver.setDestFile( zipFile );

            zipArchiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
