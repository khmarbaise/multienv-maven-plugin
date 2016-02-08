def buildLogFile = new File( basedir, "build.log");

assert buildLogFile.text.contains ('[INFO] BUILD SUCCESS')
