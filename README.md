Configuration Packager Maven Plugin
===================================

License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)


Overview
--------

 src
  +-- main
       +-- environments
             !
             +--- dev1
             +--- dev2
             +--- test1
             +--- test2
             +--- prod


 => Result will be (either zip, tar.gz, tar)
       xyz-1.0-dev1.zip
       xyz-1.0-dev1.tar.gz
       xyz-1.0-dev2.zip
       xyz-1.0-dev2.tar.gz
       xyz-1.0-test1.zip

How to identify the environments?

We should support filtering as src/main/resources
