/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.*
import java.util.*


t = new IntegrationBase()


def getProjectVersion() {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    return pom.version
}

def projectVersion = getProjectVersion();

println "Project version: ${projectVersion}"

def classifierList = [
    'dev-02',
    'qa01',
    'test02'
]

def excludedList = [
    'dev-01',
    'qa02',
    'test01'
]

def buildLogFile = new File( basedir, "build.log");

if (!buildLogFile.exists()) {
    throw new FileNotFoundException("build.log does not exists.")
}

def targetDirectory = new File (basedir, "target")
if (!targetDirectory.exists()) {
    throw new FileNotFoundException("target directory does not exists.")
}

classifierList.each { classifier ->
    def tf = new File (targetDirectory, "war-with-classifier-test-" + projectVersion + "-" + classifier + ".jar")
    println "Checking ${classifier}: " + tf.getAbsolutePath()
    if (!tf.exists()) {
        throw new FileNotFoundException("The file " + tf.getAbsolutePath() + " does not exists.")
    }
}

excludedList.each { classifier ->
    def tf = new File (targetDirectory, "war-with-classifier-test-" + projectVersion + "-" + classifier + ".jar")
    println "Checking ${classifier}: " + tf.getAbsolutePath()
    if (tf.exists()) {
        throw new Exception("The file " + tf.getAbsolutePath() + " exists.")
    }
}

def tfWar = new File (targetDirectory, "war-with-classifier-test-" + projectVersion + "-new.war")
if (!tfWar.exists()) {
    throw new FileNotFoundException("The war file " + tfWar.getAbsolutePath() + " does not exists.")
}


return true;
