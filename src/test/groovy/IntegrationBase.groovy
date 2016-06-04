import java.util.zip.*
import java.io.*
import java.util.*

class IntegrationBase {

  def getLinesFromFileWithinTheArchive(def archiveFile, def fileName) {
      def lines = []
      ZipFile zf = new ZipFile(archiveFile);
      try {
          for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements();) {
              ZipEntry ze = e.nextElement();
              String name = ze.getName();
              if (name.equals(fileName)) {
                  InputStream is = zf.getInputStream(ze);
                  lines = getLineOrientedContentFromStream(is);
                  is.close()
              }
          }
      } finally {
        zf.close();
      } 
      return lines;
  }

  def fileNameExistInArchive(def archiveFile, def fileName) {
      def result = false
      ZipFile zf = new ZipFile(archiveFile);
      try {
          for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements();) {
              ZipEntry ze = e.nextElement();
              if (ze.getName().equals(fileName)) {
                result = true
              }
          }
      } finally {
        zf.close();
      } 
      return result;
  }

  def getLineOrientedContentFromStream(def inputStream) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      def lines = []
      String line;
      while ((line = reader.readLine()) != null) {
          lines.push (line)
      }
      return lines
  }

	void checkExistenceAndContentOfAFile(file, contents) {
		if (!file.canRead()) {
			throw new FileNotFoundException( "Could not find the " + file)
		}

		def lines_to_check_in_unix_script_marker = [:]
		(0..contents.size()).each { index ->
			lines_to_check_in_unix_script_marker[index] = false
		}

		file.eachLine { file_content, file_line ->
			contents.eachWithIndex { contents_expected, index ->
				if (file_content.equals(contents_expected)) {
					lines_to_check_in_unix_script_marker[index] = true
				}
			}
		}

		contents.eachWithIndex { value, index ->
			if ( lines_to_check_in_unix_script_marker[index] == false ) {
				throw new Exception("The expected content in " + file + " couldn't be found." + contents[index])
			}
		}
	}
    
    def String convertPathIntoPlatform(String contents) {
        def result = "";
        if (File.separator.equals("\\")) {
            result = contents.replaceAll("/", "\\\\");
        } else {
            result = contents;
        }
        
        return result;
    }
}
