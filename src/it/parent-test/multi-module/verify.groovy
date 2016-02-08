import java.awt.Checkbox;
import java.io.*
import java.util.*

import org.apache.maven.model.DistributionManagement;


t = new IntegrationBase()


def getProjectVersion() {
	def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))
   
	  def allPlugins = pom.version;
   
	  return pom.version;
}
   
def projectVersion = getProjectVersion();
   
println "Project version: ${projectVersion}"
   

def buildLogFile = new File( basedir, "build.log");

return true;
