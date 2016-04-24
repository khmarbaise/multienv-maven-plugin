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

There are several scenarios where you have different configurations for 
different environments like dev, test, prod etc. (in real life there are
usually much more environments than three.).

Now you need to produce different artifacts for example war files for those
different environments. A combination of [maven-assembly-plugin and some
descriptors][blog-multiple-environments-i] will solve this. Also this
can be [enhanced for other things as well][blog-multiple-environments-ii].

The problem comes if you have more than two or three environments than the
[configuration][iterator-plugin] with
[maven-assembly-plugin][iterator-plugin-map] etc. became cumbersome.
This plugin will exactly handle such scenarios.

Example
-------

Let us assume you have several environments like `dev-01`, `dev-02`, `test-01`,
`test-02` and finally `prod`. Furthermore we make the whole situation more or
less simple and we assume having only a single module build which produces a
single `war` file as result.

To use the Configuration Maven Plugin you can simple create the following
structure in your module tree:

     src
      ├── main 
            ├── environments
                 ├── dev-01
                 │   └── first.properties
                 ├── dev-02
                 │   └── first.properties
                 ├── test-01
                 │   └── first.properties
                 ├── test-02
                 │   └── first.properties
                 └── prod
                     └── first.properties

In Result the Configuration Maven Plugin will automatically create the
appropriate war files containing the configuration file `first.properties`
which might contain some information like the database connections url etc. for
the appropriate environment.

The environment name will automatically being used as classifier for the
appropriate artifact name. So we would get the following file after running
Configuration Maven Plugin:

  * artifactId-version-dev-01.war
  * artifactId-version-dev-02.war
  * artifactId-version-test-01.war
  * artifactId-version-test-02.war
  * artifactId-version-prod.war


So next time you need to add an environment you simply solve this by adding a
new folder under `environments` which might being called `qa-01` plus the
information you would like to configure and that's it what's needed to do.
Configuration Maven Plugin will automatically identify the new environment by
search in the environment folder and producing appropriate artifacts from it.

How To Configure
----------------

To configure Configuration Maven Plugin you simply add the following
to your pom file (we assume here a war file):

``` xml

  <groupId>groupXXX</groupId>
  <artifactId>artifact</artifactId>
  <version>0.1-SNAPSHOT</version>
  <packaging>war</packaging>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>configuration-maven-plugin</artifactId>
        <version>0.1.0</version>
        <executions>
          <execution>
            <goals>
              <goal>configuration</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

Document 
 * Using filtering? 
 * Using filename/folder filtering ?
 * Different files for different environments

Advantages
----------

 * Much more convenient 
 * less configuration. 
 * Dynamically add new environments 
 * No Profiles needed.

TODO
----
 
 o Overwriting of file which exist in the original artifact? How to handle?
 o Produce an artifact only for a single environment?
   `mvn -Dconfiguration.maven.environment=test-01 clean package` 
   Is this a good idea?


[blog-multiple-environments-i]: http://blog.soebes.de/blog/2011/07/29/maven-configuration-for-multipe-environments/
[blog-multiple-environments-ii]: http://blog.soebes.de/blog/2011/08/11/maven-configuration-for-multipe-environments-ii/
[iterator-plugin]: http://khmarbaise.github.io/iterator-maven-plugin/
[iterator-plugin-map]: https://github.com/khmarbaise/iterator-maven-plugin/blob/master/src/it/mavenAssemblyPluginTest/pom.xml
