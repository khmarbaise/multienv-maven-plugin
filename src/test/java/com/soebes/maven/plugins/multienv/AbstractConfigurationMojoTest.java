package com.soebes.maven.plugins.multienv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class AbstractConfigurationMojoTest
{
    public File getMavenBaseDir()
    {
        return new File( System.getProperty( "basedir", System.getProperty( "user.dir", "." ) ) );
    }

    public File getMavenTargetDir()
    {
        return new File( getMavenBaseDir(), "target" );
    }

    private AbstractConfigurationMojo mojo;

    @BeforeTest
    public void beforeTest()
    {
        mojo = mock( AbstractConfigurationMojo.class, Mockito.CALLS_REAL_METHODS );
    }

    @Test
    public void readingTheEnvironmentsFromTheBasicIntegrationTestShouldReturnSix()
    {
        File resourceResult = new File( getMavenBaseDir(), "src/it/basicTest/src/main/environments" );
        String[] theEnvironments = mojo.getTheEnvironments( resourceResult );
        assertThat( theEnvironments ).hasSize( 6 );
    }

    @Test
    public void readingTheEnvironmentsFromTheBasicIntegrationTestShouldReturnSixNamedCorrectly()
    {
        File resourceResult = new File( getMavenBaseDir(), "src/it/basicTest/src/main/environments" );
        String[] theEnvironments = mojo.getTheEnvironments( resourceResult );
        assertThat( theEnvironments ).containsOnly( "dev-01", "dev-02", "qa01", "qa02", "test01", "test02" );
    }
}
