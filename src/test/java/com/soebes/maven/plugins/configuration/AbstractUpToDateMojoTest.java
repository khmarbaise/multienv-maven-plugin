package com.soebes.maven.plugins.configuration;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractUpToDateMojoTest
    extends TestBase
{

    public class GetVersionFromDependencyManagement
    {
        private AbstractConfigurationMojo mojo;

        private MavenProject mavenProject;

        @BeforeMethod
        public void beforeMethod()
        {
            mojo = mock( AbstractConfigurationMojo.class, Mockito.CALLS_REAL_METHODS );
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

//            Dependency resultDependency = mojo.getDependencyManagement( dependency );
//            assertThat( resultDependency.getVersion() ).isEqualTo( "1.0" );
        }

    }
}
