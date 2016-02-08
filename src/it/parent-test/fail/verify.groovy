
def getProjectVersion() {
   def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

   def allPlugins = pom.build.plugins

   def dependencies = allPlugins.plugin
   def uptodatePlugin = dependencies.find {
       item -> item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("uptodate-maven-plugin");
   }

   return uptodatePlugin.version;
}

def projectVersion = getProjectVersion();

def buildLogFile = new File( basedir, "build.log");

println "Version:" + projectVersion

assert buildLogFile.text.contains ('[ERROR] Failed to execute goal com.soebes.maven.plugins:uptodate-maven-plugin:' + projectVersion + ':parent (parent-check) on project root: There is a more up-to-date version ( 0.2 ) of the parent available. -> [Help 1]')

