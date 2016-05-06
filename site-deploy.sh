mvn -Prun-its clean verify
mvn site site:stage 
mvn scm-publish:publish-scm
