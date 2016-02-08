package com.soebes.maven.plugins.uptodate;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

public abstract class AbstractUpToDateMojo
    extends AbstractMojo
{

    /**
     * You can skip the execution of uptodate plugin in cases
     * where you might encounter a failure.
     */
    @Parameter( defaultValue = "false", property = "uptodate.skip" )
    private boolean skip;

    /**
     * The project currently being build.
     */
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    /**
     * The project's remote repositories to use for the resolution.
     */
    @Parameter( defaultValue = "${project.remoteProjectRepositories}", readonly = true )
    private List<RemoteRepository> remoteRepos;

    /**
     * The current Maven session.
     */
    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    /**
     * The repositorySystemSession.
     */
    @Parameter( defaultValue = "${repositorySystemSession}", readonly = true )
    private RepositorySystemSession repositorySystemSession;

    @Component
    private RepositorySystem repository;

    public boolean isSkip()
    {
        return skip;
    }

    public void setSkip( boolean skip )
    {
        this.skip = skip;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public List<RemoteRepository> getRemoteRepos()
    {
        return remoteRepos;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public RepositorySystemSession getRepositorySystemSession()
    {
        return repositorySystemSession;
    }

    public RepositorySystem getRepository()
    {
        return repository;
    }

    protected List<Version> getNewerVersionsOfArtifact( String groupId, String artifactId, String version,
                                                        String classifier, String extension )
        throws VersionRangeResolutionException
    {
        // Create a version range from our current version..
        String versionRange = "[" + version + ",)";

        String requestExtension = ( extension == null ) ? "" : ":" + extension;
        String requestClassifier = ( classifier == null ) ? "" : ":" + classifier;

        // {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}, must not be {@code null}.
        Artifact artifact =
            new DefaultArtifact( groupId + ":" + artifactId + requestExtension + requestClassifier + ":" + versionRange );
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.setRepositories( remoteRepos );

        VersionRangeResult rangeResult = repository.resolveVersionRange( repositorySystemSession, rangeRequest );
        List<Version> versions = rangeResult.getVersions();

        return versions;
    }

    protected String join( final List<Version> versions )
    {
        StringBuilder sb = new StringBuilder();
        for ( Iterator<Version> iterator = versions.iterator(); iterator.hasNext(); )
        {
            Version version = iterator.next();
            sb.append( version );
            if ( iterator.hasNext() )
            {
                sb.append( "," );
            }
        }
        return sb.toString();
    }

    private String getArtifactIdentification( Dependency dependency )
    {
        return dependency.getGroupId() + ":" + dependency.getArtifactId();
    }

    protected Dependency getVersionFromDependencyManagement( Dependency dependency )
    {
        Dependency result = dependency;
        if ( hasDependencyManagement() )
        {
            List<Dependency> depMgmtList = getMavenProject().getDependencyManagement().getDependencies();

            String dependencyKey = getArtifactIdentification( dependency );
            for ( Dependency item : depMgmtList )
            {
                String itemKey = getArtifactIdentification( item );
                getLog().debug( "Checking " + getArtifactIdentification( item ) );
                if ( dependencyKey.equals( itemKey ) )
                {
                    result = item;
                }

            }
        }
        else
        {
            getLog().error( "No dependency management" );
            // What should we do?
            // Fail!!!
        }
        return result;
    }

    protected Dependency getDependencyManagement( Dependency dependency )
    {
        if ( hasVersion( dependency ) )
        {
            return dependency;
        }
        else
        {
            return getVersionFromDependencyManagement( dependency );
        }
    }

    protected boolean hasDependencyManagement()
    {
        return getMavenProject().getDependencyManagement() != null;
    }

    protected boolean hasVersion( Dependency dependency )
    {
        return (dependency.getVersion() != null) && !dependency.getVersion().isEmpty();
    }

}
