Configuration Maven Plugin
==========================

License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)


Overview
--------

Assuming the following structure:

    src
     +-- main
          +-- environments
                !
                +--- dev01
                +--- dev02
                +--- test01
                +--- test02
                +--- prod


The given folders under `environments` give the resulting packages.
The folder like `dev01` will result in a package which contains
all files which are located in the `dev01` folder.


 => Result will be (either zip, tar.gz, tar)

 * xyz-1.0-dev1.zip
 * xyz-1.0-dev1.tar.gz
 * xyz-1.0-dev2.zip
 * xyz-1.0-dev2.tar.gz
 * xyz-1.0-test1.zip

