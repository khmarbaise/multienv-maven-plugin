package com.soebes.maven.plugins.multienv;

import java.io.File;

/**
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
public class UnitTestBase
{

    public File getMavenBaseDir()
    {
        return new File( System.getProperty( "basedir", System.getProperty( "user.dir", "." ) ) );
    }

    public File getMavenTargetDir()
    {
        return new File( getMavenBaseDir(), "target" );
    }

}
