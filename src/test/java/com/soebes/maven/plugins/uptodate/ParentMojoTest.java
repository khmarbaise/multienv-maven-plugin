package com.soebes.maven.plugins.uptodate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;
import org.testng.annotations.Test;

public class ParentMojoTest
    extends TestBase
{

    @Test
    public void shouldNotFailCauseNoParentExists()
        throws MojoExecutionException, MojoFailureException, VersionRangeResolutionException
    {
        ParentMojo mojo = mock( ParentMojo.class );
        createProjectWithoutParent( mojo );
        doCallRealMethod().when( mojo ).execute();
        mojo.execute();

    }

    @Test
    public void shouldNotFailCauseParentExistsWithNoNewerVersion()
        throws MojoExecutionException, MojoFailureException, VersionRangeResolutionException
    {
        ParentMojo mojo = mock( ParentMojo.class );
        createProjectWithParent( mojo );

        List<Version> versionsList = createVersionList( "1.0" );
        when( mojo.getNewerVersionsOfArtifact( anyString(), anyString(), anyString(), anyString(), anyString() ) ).thenReturn( versionsList );

        doCallRealMethod().when( mojo ).execute();
        mojo.execute();

    }

    @Test( expectedExceptions = MojoExecutionException.class )
    public void shouldFailCauseParentExistsWithNewerVersion()
        throws MojoExecutionException, MojoFailureException, VersionRangeResolutionException
    {
        ParentMojo mojo = mock( ParentMojo.class );
        createProjectWithParent( mojo );

        List<Version> versionsList = createVersionList( "1.0", "1.1" );
        when( mojo.getNewerVersionsOfArtifact( anyString(), anyString(), anyString(), anyString(), anyString() ) ).thenReturn( versionsList );

        doCallRealMethod().when( mojo ).execute();
        mojo.execute();
    }

    private MavenProject createParentProject()
    {
        MavenProject parentProject = mock( MavenProject.class );
        when( parentProject.getGroupId() ).thenReturn( "com.soebes.maven.plugins.parent" );
        when( parentProject.getArtifactId() ).thenReturn( "parent-01" );
        when( parentProject.getVersion() ).thenReturn( "1.0" );
        return parentProject;
    }

    private void createProjectWithoutParent( ParentMojo mojo )
    {
        MavenProject project = createProject( mojo );

        when( project.getParent() ).thenReturn( null );
    }

    private void createProjectWithParent( ParentMojo mojo )
    {
        MavenProject project = createProject( mojo );

        MavenProject parentProject = createParentProject();
        when( project.getParent() ).thenReturn( parentProject );
    }

    private MavenProject createProject( ParentMojo mojo )
    {
        when( mojo.getLog() ).thenReturn( mock( Log.class ) );

        MavenSession session = mock( MavenSession.class );
        MavenProject project = mock( MavenProject.class );
        when( session.getCurrentProject() ).thenReturn( project );
        when( mojo.getMavenProject() ).thenReturn( project );
        when( mojo.getMavenSession() ).thenReturn( session );

        when( project.isExecutionRoot() ).thenReturn( true );
        return project;
    }

}
