Common Maven Enforcer Rules
===========================

The maven enforcer rules keeps your complex maven project clean and enforces best practice conventions in maven pom.
This helps you to save time and avoid annoying garden keeping tasks.

Configuration
-------------

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>
   <artifactId>my.enforcer.configuration.project</artifactId>
   <packaging>pom</packaging>
   <version>1.1.0-SNAPSHOT</version>
   <build>
     <plugins>
     <plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-enforcer-plugin</artifactId>
   <version>1.0.1</version>
   <executions>
      <execution>
         <id>enforce</id>
         <configuration>
            <rules>
               <!-- DependencyConvergence / -->
               <requireMavenVersion>
                  <version>3.0</version>
               </requireMavenVersion>
               <myCustomRule implementation="de.adorsys.cmer.NoDependencyVersions">
                  <allowedGroupPrefix>my.enforced.project.groupid</allowedGroupPrefix>
                  <ignoreMasterProjectGroupId>master.project.groupid</ignoreMasterProjectGroupId>
               </myCustomRule>
               <myCustomRule
                     implementation="de.adorsys.cmer.GroupIdAndVersionInheritance">
                     <declaringProjectArtefactId>my.enforcer.configuration.project</declaringProjectArtefactId>
               </myCustomRule>
               <myCustomRule
                     implementation="de.adorsys.cmer.InheritedDependencyScopeOverridden"/>
               <myCustomRule
                     implementation="de.adorsys.cmer.ProjectNamingConverntions">
                     <allowedGroupPrefix>my.enforced.project.groupid</allowedGroupPrefix>
              </myCustomRule>
            </rules>
         </configuration>
         <goals>
            <goal>enforce</goal>
         </goals>
      </execution>
   </executions>
   <dependencies>
      <dependency>
         <groupId>de.bsh.fo.maven</groupId>
         <artifactId>maven.enforcer-rules</artifactId>
         <version>1.1.0-SNAPSHOT</version>
      </dependency>
   </dependencies>
</plugin>