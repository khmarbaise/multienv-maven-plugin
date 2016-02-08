
def getProjectVersion() {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    def allPlugins = pom.build.plugins

    def dependencies = allPlugins.plugin

    def uptodateModule = dependencies.find { item ->
        item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("uptodate-maven-plugin");
    }

    return uptodateModule.version;
}


def projectVersion = getProjectVersion()

println "Version: ${projectVersion}"

def buildLogFile = new File( basedir, "build.log");

assert buildLogFile.text.contains ('[ERROR] Failed to execute goal com.soebes.maven.plugins:uptodate-maven-plugin:' + projectVersion + ':dependency (dependency-validate) on project root: There is a more up-to-date version ( 0.2 ) of the dependency com.soebes.maven.plugins.test.integration:dep-01:0.1 available. -> [Help 1]')

