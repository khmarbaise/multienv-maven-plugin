def buildLogFile = new File( basedir, "build.log");
assert buildLogFile.text.contains ('[DEBUG] com.soebes.maven.plugins.test.integration:parent:0.2 existing versions:1 version:0.2' )

