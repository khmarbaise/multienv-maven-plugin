package com.soebes.maven.plugins.multienv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class AbstractMultiEnvMojoTest
    extends UnitTestBase
{
    public class GetTheEnvironmentTest
    {

        private AbstractMultiEnvMojo mojo;

        @BeforeTest
        public void beforeTest()
        {
            mojo = mock( AbstractMultiEnvMojo.class, Mockito.CALLS_REAL_METHODS );
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

    public class GetArchiveFileTest
    {
        private AbstractMultiEnvMojo mojo;

        private File mockFile;

        private static final String NON_EMPTY_STRING = "NON_EMPTY";

        private static final String EMPTY_STRING = "";

        @BeforeTest
        public void beforeTest()
        {
            mockFile = mock( File.class );
            mojo = mock( AbstractMultiEnvMojo.class, Mockito.CALLS_REAL_METHODS );
        }

        @Test( expectedExceptions = {
            IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "basedir is not allowed to be null" )
        public void getArchiveFileShouldFailWithIAEFileNull()
        {
            mojo.getArchiveFile( null, NON_EMPTY_STRING, NON_EMPTY_STRING, NON_EMPTY_STRING );
        }

        @Test( expectedExceptions = {
            IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "finalName is not allowed to be null" )
        public void getArchiveFileShouldFailWithIAEFinalNameNull()
        {
            mojo.getArchiveFile( mockFile, null, NON_EMPTY_STRING, NON_EMPTY_STRING );
        }

        @Test( expectedExceptions = {
            IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "archiveExt is not allowed to be null" )
        public void getArchiveFileShouldFailWithIAEArchiveExtNull()
        {
            mojo.getArchiveFile( mockFile, NON_EMPTY_STRING, NON_EMPTY_STRING, null );
        }

        @Test( expectedExceptions = {
            IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "archiveExt is not allowed to be empty." )
        public void getArchiveFileShouldFailWithIAEClassifierNotNull()
        {
            mojo.getArchiveFile( mockFile, NON_EMPTY_STRING, NON_EMPTY_STRING, EMPTY_STRING );
        }

        public void getArchiveFileShouldNotFailWithIAEIfAllParameters()
        {
            mojo.getArchiveFile( mockFile, NON_EMPTY_STRING, NON_EMPTY_STRING, NON_EMPTY_STRING );
        }

        @Test( expectedExceptions = {
            IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "finalName is not allowed to be empty." )
        public void getArchiveFileShouldFailWithFinalNameEmpty()
        {
            mojo.getArchiveFile( mockFile, EMPTY_STRING, NON_EMPTY_STRING, NON_EMPTY_STRING );
        }

        @Test
        public void getArchiveFileShouldReturnFinalNameJar()
        {
            File result = mojo.getArchiveFile( new File( "." ), "finalName", null, "jar" );
            assertThat( result.getName() ).isEqualTo( "finalName.jar" );
        }

        @Test
        public void getArchiveFileShouldReturnFinalNameWithClassifierJar()
        {
            File result = mojo.getArchiveFile( new File( "." ), "finalName", "cls", "jar" );
            assertThat( result.getName() ).isEqualTo( "finalName-cls.jar" );
        }
    }
}