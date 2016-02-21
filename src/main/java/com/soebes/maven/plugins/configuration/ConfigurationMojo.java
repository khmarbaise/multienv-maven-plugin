package com.soebes.maven.plugins.configuration;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.archiver.Archiver;
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

    @Component( role = Archiver.class, hint = "jar" )
    private MavenArchiver archiver;

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
    }

}
