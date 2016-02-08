package com.soebes.maven.plugins.uptodate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.aether.version.Version;

public class TestBase
{

    private Version createMockVersion( String version )
    {
        Version v = mock( Version.class );
        when( v.toString() ).thenReturn( version );
        return v;
    }

    protected Dependency createMockDependency( String groupId, String artifactId, String version )
    {
        Dependency dep = mock( Dependency.class );
        when( dep.getGroupId() ).thenReturn( groupId );
        when( dep.getArtifactId() ).thenReturn( artifactId );
        when( dep.getVersion() ).thenReturn( version );

        return dep;
    }

    protected List<Version> createVersionList( String... versions )
    {
        List<Version> versionsList = new ArrayList<Version>();
        for ( int i = 0; i < versions.length; i++ )
        {
            Version v = createMockVersion( versions[i] );
            versionsList.add( v );
        }
        return versionsList;
    }

}
