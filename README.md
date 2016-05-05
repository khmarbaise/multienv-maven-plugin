Multi Environment Maven Plugin
==============================

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/multienv-maven-plugin.svg?label=License)](http://www.apache.org/licenses/)
[![Build Status](https://travis-ci.org/khmarbaise/multienv-maven-plugin.svg?branch=master)](https://travis-ci.org/khmarbaise/multienv-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.maven.plugins/multienv-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Ccom.soebes.maven.plugins)

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

To use the MultiEnv Maven Plugin you can simple create the following
structure in your module:

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

In result the MultiEnv Maven Plugin will automatically create the
appropriate war files containing the configuration file `first.properties`
which might contain some information like the database connections url etc. for
the appropriate environment.

The environment name (folder name) will automatically being used as classifier
for the appropriate artifact. So we would get the following files after
running MultiEnv Maven Plugin via ([assuming you have configured it
correctly](README.md#how-to-configure)):

```
mvn clean package
```

  * artifactId-version-dev-01.war
  * artifactId-version-dev-02.war
  * artifactId-version-test-01.war
  * artifactId-version-test-02.war
  * artifactId-version-prod.war


If you need to add a new environment this can simply being solved by adding a
new folder under `environments` which might being called `qa-01` plus the
information you would like to configure and that's it.  Configuration Maven
Plugin will automatically identify the new environment by search in the
environment folder and producing appropriate artifacts out of it.

How To Configure
----------------

To configure MultiEnv Maven Plugin you simply add the following
to your pom file (we assume here a war file):

``` xml

  <groupId>groupId</groupId>
  <artifactId>artifactId</artifactId>
  <version>0.1-SNAPSHOT</version>
  <packaging>war</packaging>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>multienv-maven-plugin</artifactId>
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

Filtering
---------

Based on the given folder structure files like `first.properties` etc. will be
filtered before they are packaged into the resulting artifacts. This means you
can use things like `${project.version}` in your files or other self defined
properties.


Document:

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

 * MultiEnv Maven Plugin in a different maven project within multi module
   build? How does that work? 
 * Overwriting of file which exist in the original artifact? How to handle?
 * Produce an artifact only for a single environment?
   `mvn -Dmultienv.environment=test-01 clean package` 
   Is this a good idea?
 o Create only separate environment package for each environment.
   instead of merging it with the current package.


[blog-multiple-environments-i]: http://blog.soebes.de/blog/2011/07/29/maven-configuration-for-multipe-environments/
[blog-multiple-environments-ii]: http://blog.soebes.de/blog/2011/08/11/maven-configuration-for-multipe-environments-ii/
[iterator-plugin]: http://khmarbaise.github.io/iterator-maven-plugin/
[iterator-plugin-map]: https://github.com/khmarbaise/iterator-maven-plugin/blob/master/src/it/mavenAssemblyPluginTest/pom.xml
