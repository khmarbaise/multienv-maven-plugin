Overview
========


Let us assume you have several environments like `dev-01`, `dev-02`, `test-01`,
`test-02` and finally `prod`. Furthermore we make the whole situation more or
less simple and we assume having only a single module build which produces a
single `war` file as result.

To use the MultiEnv Maven Plugin you can simple create the following
supplemental structure in your module apart of the war module configuration:

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
appropriate `war` files containing the configuration file `first.properties`
which might contain some information like the database connections url etc. for
the appropriate environment.

The environment name (directory name) will automatically being used as classifier
for the appropriate artifact. So we would get the following files after
running MultiEnv Maven Plugin via ([assuming you have configured it
correctly](./usage.html)):

```
mvn clean package
```

  * artifactId-version-dev-01.war
  * artifactId-version-dev-02.war
  * artifactId-version-test-01.war
  * artifactId-version-test-02.war
  * artifactId-version-prod.war

If you need to add a new environment this can simply achieved by adding
the appropriate directory under `environments` which can be named for example
`qa-01` plus the configuration you would like to add there.

A new call to `mvn clean package` will identify the new environment (directory)
and now produces a supplemental package for the `qa-01` environment as well.

This would result into the following artifact list:

  * artifactId-version-dev-01.war
  * artifactId-version-dev-02.war
  * artifactId-version-test-01.war
  * artifactId-version-test-02.war
  * artifactId-version-prod.war
  * artifactId-version-qa-01.war

Those artifacts are ready to be deployed to the appropriate environment
(Of course the application inside must handle the reading of the configuration
files).
