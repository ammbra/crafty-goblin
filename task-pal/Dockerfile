# Example of custom Java runtime using jlink in a multi-stage container build

FROM container-registry.oracle.com/java/openjdk:21-oraclelinux8 as build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
	--add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql.rowset,jdk.jfr,jdk.management,jdk.net,jdk.unsupported,jdk.jdwp.agent \
	--no-man-pages \
	--no-header-files \
	--output /javaruntime

ARG MAVEN_VERSION=3.9.4
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
 && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "/home/.m2"

COPY . /code
RUN echo '{ "allow_root": true }' > rm -Rf /code/target && \
	cd /code/ && mvn verify

# Define your base image
FROM container-registry.oracle.com/os/oraclelinux:9

ENV JAVA_HOME /usr/java/openjdk-21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=build /javaruntime $JAVA_HOME

# Continue with your application deployment
COPY --from=build ./code/target/task-pal.jar /task-pal.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENV JDK_JAVA_OPTIONS "--enable-preview"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/task-pal.jar"]
