package com.soebes.maven.plugins.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiver;
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

	/**
	 * The JAR archiver needed for archiving the environments.
	 */
	@Component( role = Archiver.class, hint = "jar" )
	private JarArchiver jarArchiver;
	
    @Component( role = UnArchiver.class, hint = "war" )
	private UnArchiver unArchiver;

	@Component
	private ArchiverManager manager;

	public void execute()
		throws MojoExecutionException, MojoFailureException
	{
	    
	    File unpackFolder = new File(getOutputDirectory(), "unpack");
	    unpackFolder.mkdirs();
	    
	    unarchiveFile( getMavenProject().getArtifact().getFile(), unpackFolder );
	    
		DirectoryScanner ds = new DirectoryScanner();
		ds.setBasedir( getSourceDirectory() );
		ds.setExcludes( new String[] { ".", "" } ); // Work a round ?
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
					// createGZIPArchive( folder );
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

	private void unarchiveFile ( File sourceFile, File destDirectory ) {
        unArchiver.setSourceFile( sourceFile );
        unArchiver.setUseJvmChmod( true);
        unArchiver.setDestDirectory( destDirectory );
        unArchiver.setOverwrite( true );
        unArchiver.extract();
	    
	}

	private void createArchiveFile( String folder )
		throws NoSuchArchiverException, IOException
	{
		try
		{
			final MavenArchiver mavenArchiver = new MavenArchiver();

			mavenArchiver.setArchiver( jarArchiver );
			jarArchiver.addFileSet( new DefaultFileSet( new File( getSourceDirectory(), folder ) ) );

			File zipFile = new File( getOutputDirectory(), folder + "-result.jar" );
			mavenArchiver.setOutputFile( zipFile );
			mavenArchiver.createArchive( getMavenSession(), getMavenProject(), getArchive() );
		}
		catch ( ArchiverException e )
		{
			getLog().error( "Archiver could not created.", e );
		}
		catch ( ManifestException e )
		{
			getLog().error( "Manifest Exception.", e );
		}
		catch ( DependencyResolutionRequiredException e )
		{
			getLog().error( "DependencyResolutionRequiredException:", e );
		}

	}
}
