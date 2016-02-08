package com.soebes.maven.plugins.uptodate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.version.Version;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbstractUpToDateMojoTest
    extends TestBase
{

    public class GetVersionFromDependencyManagement
    {
        private AbstractUpToDateMojo mojo;

        private MavenProject mavenProject;

        @BeforeMethod
        public void beforeMethod()
        {
            mojo = mock( AbstractUpToDateMojo.class, Mockito.CALLS_REAL_METHODS );
            mavenProject = mock( MavenProject.class );
            // The following will suppress logging outputs during the unit tests.
            when( mojo.getLog() ).thenReturn ( mock ( Log.class ) );
            when( mojo.getMavenProject() ).thenReturn( mavenProject );
        }

        @Test
        public void shouldReturnOriginalyGivenVersion()
        {
            Dependency dependency = mock( Dependency.class );
            when( dependency.getVersion() ).thenReturn( "1.0" );

            DependencyManagement dependencyManagement = mock( DependencyManagement.class );
            when( mavenProject.getDependencyManagement() ).thenReturn( dependencyManagement );

            Dependency resultDependency = mojo.getDependencyManagement( dependency );
            assertThat( resultDependency.getVersion() ).isEqualTo( "1.0" );
        }

        @Test
        public void shouldReturnVersionFromDependencyManagement()
        {
            DependencyManagement dependencyManagement = mock( DependencyManagement.class );
            List<Dependency> depMgmtList = new ArrayList<Dependency>();

            Dependency dep1 = mock( Dependency.class );
            when( dep1.getGroupId() ).thenReturn( "com.soebes.maven" );
            when( dep1.getArtifactId() ).thenReturn( "first-artifact" );
            when( dep1.getVersion() ).thenReturn( "2.0" );
            depMgmtList.add( dep1 );

            when( dependencyManagement.getDependencies() ).thenReturn( depMgmtList );
            when( mavenProject.getDependencyManagement() ).thenReturn( dependencyManagement );

            Dependency dependency = mock( Dependency.class );
            when( dependency.getGroupId() ).thenReturn( "com.soebes.maven" );
            when( dependency.getArtifactId() ).thenReturn( "first-artifact" );
            when( dependency.getVersion() ).thenReturn( null );

            Dependency resultDependency = mojo.getVersionFromDependencyManagement( dependency );
            assertThat( resultDependency.getVersion() ).isEqualTo( "2.0" );
        }
    }

    public class HasDependencyManagement
    {
        private AbstractUpToDateMojo mojo;

        private MavenProject mavenProject;

        @BeforeMethod
        public void beforeMethod()
        {
            mojo = mock( AbstractUpToDateMojo.class, Mockito.CALLS_REAL_METHODS );
            mavenProject = mock( MavenProject.class );
            when( mojo.getMavenProject() ).thenReturn( mavenProject );
        }

        @Test
        public void hasDependencyManagementShouldReturnFalseWithNullInDependencyManagement()
        {
            when( mavenProject.getDependencyManagement() ).thenReturn( null );

            assertThat( mojo.hasDependencyManagement() ).isFalse();
        }

        @Test
        public void hasDependencyManagementShouldReturnTrueWithDependencyManagement()
        {
            DependencyManagement dependencyManagement = mock( DependencyManagement.class );
            when( mavenProject.getDependencyManagement() ).thenReturn( dependencyManagement );

            assertThat( mojo.hasDependencyManagement() ).isTrue();
        }
    }

    public class HasVersion
    {
        private AbstractUpToDateMojo mojo;

        private Dependency dependency;

        @BeforeMethod
        public void beforeMethod()
        {
            mojo = mock( AbstractUpToDateMojo.class, Mockito.CALLS_REAL_METHODS );
            dependency = mock( Dependency.class );
        }

        @Test
        public void hasVersionShouldReturnTrueWithGivenVersion()
        {
            when( dependency.getVersion() ).thenReturn( "1.0" );

            assertThat( mojo.hasVersion( dependency ) ).isTrue();
        }

        @Test
        public void hasVersionShouldReturnTrueWithNullForVersion()
        {
            when( dependency.getVersion() ).thenReturn( null );

            assertThat( mojo.hasVersion( dependency ) ).isFalse();
        }
    }

    public class VersionList
    {
        private AbstractUpToDateMojo mojo;

        @BeforeMethod
        public void beforeMethod()
        {
            mojo = mock( AbstractUpToDateMojo.class, Mockito.CALLS_REAL_METHODS );
        }

        @Test
        public void shouldResultInASingleElementWithoutComma()
        {
            List<Version> versionsList = createVersionList( "1.0" );

            assertThat( mojo.join( versionsList ) ).isEqualTo( "1.0" );
        }

        @Test
        public void shouldResultInListJoinedByCommaTwoElements()
        {
            List<Version> versionsList = createVersionList( "1.0", "1.1" );

            assertThat( mojo.join( versionsList ) ).isEqualTo( "1.0,1.1" );
        }

        @Test
        public void shouldResultInListJoinedByCommaThreeElements()
        {
            List<Version> versionsList = createVersionList( "1.0", "1.1", "1.2" );

            assertThat( mojo.join( versionsList ) ).isEqualTo( "1.0,1.1,1.2" );
        }

    }
}
