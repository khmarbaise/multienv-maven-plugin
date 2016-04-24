Configuration Maven Plugin
==========================

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/configuration-maven-plugin.svg?label=License)](http://www.apache.org/licenses/)
[![Build Status](https://travis-ci.org/khmarbaise/configuration-maven-plugin.svg?branch=master)](https://travis-ci.org/khmarbaise/configuration-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.maven.plugins/configuration-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Ccom.soebes.maven.plugins)

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

