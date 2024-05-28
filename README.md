# mkh-microservice-gradle-plugin
This is a Gradle Plugin for SpringBoot Microservice Development

## Setup
Built with Gradle Wrapper 8.7, IntelliJ 2023.3.1 CE 

## Usage

```kotlin
plugins {
    id("io.github.khalid64927.gradle.mkh.microservice")
}
```

Required gradle properties
```properties
# Required by JavaPlugin and MKHMicroservicePlugin
mkh.javaVersion="17"
# for GraalVM image
mkh.native=false
# Sonar
mkh.sonar.projectKey=""
mkh.sonar.url=""
# If running on CI start
mkh.sonar.pullrequest.key=""
mkh.sonar.pullrequest.branch=""
mkh.sonar.pullrequest.base=""
# If running on CI end
mkh.sonar.branch.name=""
# optional exclusions
mkh.sonar.exclusions=""
# optional sources
mkh.sonar.sources=""
# optional test directory path
mkh.sonar.tests=""

# required by OSSPublicationPlugin for scan task
mkh.publish.name="mkh-gradle-plugin"
mkh.publish.description="This is Gradle Plugin implemented in Kotlin"
mkh.publish.repo.org="com.github.khalid64927.gradle"
mkh.publish.repo.name="mkh-microservice-plugin"
mkh.publish.license=""
mkh.publish.developers="khalid64927|Mohammed Khalid Hamid|khalid64927@gmail.com"
mkh.publish.repo.url="https://github.com/khalid64927/mkh-microservice-plugin"
mkh.publish.repo.sshUrl="scm:git:ssh://github.com/khalid64927/mkh-microservice-plugin.git"
# required by OSSSonatypeNexusScanPlugin for scan task start
mkh.nexus.applicationId=""
mkh.nexus.IQUrl=""
mkh.nexusUsername=<username>
mkh.nexusPassword="<pwd>"
# required by OSSSonatypeNexusScan, NexusPublicationPlugin for scan and publish task end


```


## License

    Copyright 2024 Mohammed Khalid Hamid.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.