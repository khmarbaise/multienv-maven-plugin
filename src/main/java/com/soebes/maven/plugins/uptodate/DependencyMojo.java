package com.soebes.maven.plugins.uptodate;

import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;

/**
 * Will check dependencies of your project and fail the build if they are not up-to-date.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "dependency", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true )
public class DependencyMojo
    extends AbstractUpToDateMojo
{

    /**
     * Define the list of dependencies you would like check if they are up-to-date. If this list is not defined or empty
     * the plugin will check all project dependencies.
     */
    @Parameter
    private List<Dependency> dependencies;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( isSkip() )
        {
            getLog().info( " Skipping execution based on user request." );
            return;
        }

        try
        {
            List<Dependency> dependencies = Collections.emptyList();
            if ( dependenciesGiven() )
            {
                dependencies = getDependencies();
            }
            else
            {
                dependencies = getMavenProject().getDependencies();
            }

            for ( Dependency dependency : dependencies )
            {
                getLog().debug( "Dependency to check: G:" + dependency.getGroupId() + " A:" + dependency.getArtifactId() + " V:" + dependency.getVersion() );
                dependency = getDependencyManagement( dependency );
                String id = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
                List<Version> versions =
                    getNewerVersionsOfArtifact( dependency.getGroupId(), dependency.getArtifactId(),
                                                dependency.getVersion(), dependency.getClassifier(),
                                                dependency.getType() );

                getLog().debug( "Dependency: " + id + " Number of existing versions:" + versions.size() + " version:"
                                    + join( versions ) );

                if ( versions.size() > 1 )
                {
                    Version newest = versions.get( versions.size() - 1 );
                    throw new MojoExecutionException( "There is a more up-to-date version ( " + newest
                        + " ) of the dependency " + id + " available." );
                }
            }
        }
        catch ( VersionRangeResolutionException e )
        {
            getLog().error( "VersionRangeRosolutionException", e );
        }

    }

    private boolean dependenciesGiven()
    {
        return getDependencies() != null && !getDependencies().isEmpty();
    }

    public List<Dependency> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }

}
