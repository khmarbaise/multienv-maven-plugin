Multi Environment Maven Plugin
==============================

[![Apache License, Version 2.0, January 2004][shield-license]][apache-license]
[![Build Status][build-status]][travis-ci]
[![Maven Central][shield-central]][maven-central]

License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)


Overview
--------

There are several scenarios where you have different configurations for 
different environments like dev, test, prod etc. (in real life there are
usually more environments than three.).

Now you need to produce different artifacts for example war files for those
different environments. A combination of [maven-assembly-plugin and some
descriptors][blog-multiple-environments-i] will solve this. Also this
can be [enhanced for other things as well][blog-multiple-environments-ii].

The problem becomes worse if you have more than two or three environments than the
[configuration][iterator-plugin] with
[maven-assembly-plugin][iterator-plugin-map] etc. became cumbersome.
This plugin will exactly handle such scenarios.

The scenarios are the following. Producing artifacts which include the
configuration for each [environment](README.md#example-1): or artifacts which contain only the 
[configuration](README.md#example-2): for the appropriate environment.

Example 1
---------

Let us assume you have several environments like `dev-01`, `dev-02`, `test-01`,
`test-02` and finally `prod`. We will make the situation simpler for this
example and assume having only a single module build which produces a
single `war` file as a result.

The prerequisite to use the MultiEnv Maven Plugin is to create
a directory structure similar like the following:

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
(just a single file for brevity in this example) which might contain some information like
 the database connections url etc. for the appropriate environment.

You can of course put several different files into the different environment
directories. It is also possible to create a directory structure under the appropriate
environment. This will also be packaged into the resulting artifact.

The environment name (directory name `dev-01`, `dev-02`, `test-01` etc.) will
automatically being used as classifier for the appropriate artifact. So we
would get the following files after running MultiEnv Maven Plugin via 
([assuming you have configured it correctly](README.md#how-to-configure)):

```
mvn clean package
```

  * artifactId-version-dev-01.war
  * artifactId-version-dev-02.war
  * artifactId-version-test-01.war
  * artifactId-version-test-02.war
  * artifactId-version-prod.war


If you need to add a new environment this can simply being achieved by adding a
new directory under `environments` which might being called `qa-01` plus the
information you would like to configure and that's it. MultiEnv Maven
Plugin will automatically identify the new environment by searching in the
environment directory and producing an appropriate artifact out of it.

Those above packages contain the original `war` file content as well
as the supplemental files/directories which have been given for the
appropriate environments.

Example 2
---------

In this example we would like to create configuration artifacts for 
each environment.

The configuration looks exactly the same in [Example 1](README.md#example-1)
except for the used goal. So you need to change the 
goal from `environment` to `configuration` in the plugin configuration.
By using the:

```
mvn clean package
```

you will produce the following artifacts:

  * artifactId-version-dev-01.jar
  * artifactId-version-dev-02.jar
  * artifactId-version-test-01.jar
  * artifactId-version-test-02.jar
  * artifactId-version-prod.jar

As you might already realized that those files are not `war` files. The
files are jar files which contain the configuration for each environment.


How To Configure
----------------

To configure MultiEnv Maven Plugin you simply add the following
to your pom file (we assume here a war file):

``` xml

  <groupId>groupId</groupId>
  <artifactId>artifactId</artifactId>
  <version>0.1.0-SNAPSHOT</version>
  <packaging>war</packaging>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>multienv-maven-plugin</artifactId>
        <version>0.3.1</version>
        <executions>
          <execution>
            <goals>
              <goal>environment</goal>
            </goals>
          </execution>
          <configuration>
            <enabled>true</enabled><!-- by default true, but you can disable it with some property for example -->
          </configuration>
        </executions>
      </plugin>
    </plugins>
  </build>
```

Filtering
---------

Based on the given directory structure files like `first.properties` etc. will be
filtered before they are packaged into the resulting artifacts. This means you
can use things like `${project.version}` in your files or other self defined
properties.


Document:

 * Using filename/directory filtering ?
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
 * Do filtering for each environment separately. So we could inject
   the name of the environment. This can be used put the name of the
   environment into the resulting files via filtering.

[blog-multiple-environments-i]: http://blog.soebes.de/blog/2011/07/29/maven-configuration-for-multipe-environments/
[blog-multiple-environments-ii]: http://blog.soebes.de/blog/2011/08/11/maven-configuration-for-multipe-environments-ii/
[iterator-plugin]: http://khmarbaise.github.io/iterator-maven-plugin/
[iterator-plugin-map]: https://github.com/khmarbaise/iterator-maven-plugin/blob/master/src/it/mavenAssemblyPluginTest/pom.xml
[maven-central]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.soebes.maven.plugins%22%20a%3A%22multienv-maven-plugin%22
[apache-license]: http://www.apache.org/licenses/
[travis-ci]: https://travis-ci.org/khmarbaise/multienv-maven-plugin
[build-status]: https://travis-ci.org/khmarbaise/multienv-maven-plugin.svg?branch=master
[shield-central]: https://img.shields.io/maven-central/v/com.soebes.maven.plugins/multienv-maven-plugin.svg?label=Maven%20Central
[shield-license]: https://img.shields.io/github/license/khmarbaise/iterator-maven-plugin.svg?label=License
