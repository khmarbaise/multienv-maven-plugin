package com.soebes.maven.plugins.configuration;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
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

    // @Component( role = Archiver.class, hint = "jar" )
    @Inject
    private JarArchiver zipArchiver;

    @Inject
    private MavenArchiveConfiguration configuration;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( getSourceDirectory() );
        ds.setExcludes( new String[] { "." } );
        ds.addDefaultExcludes();

        ds.scan();

        String[] includedDirectories = ds.getIncludedDirectories();
        for ( String folder : includedDirectories )
        {
            getLog().info( "Environment Folder: '" + folder + "'" );
        }

        zipArchiver.addDirectory( new File(getSourceDirectory(), includedDirectories[1]) );
        
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( zipArchiver );
        File zipFile = new File( getOutputDirectory(), "result.zip" );
        archiver.setOutputFile( zipFile );
        try
        {
            archiver.createArchive( getMavenSession(), getMavenProject(), configuration );
        }
        catch ( ArchiverException | ManifestException | IOException | DependencyResolutionRequiredException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
