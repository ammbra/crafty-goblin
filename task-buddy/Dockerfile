################ Build & Dev ################
# Build stage will be used:
# - for building the application for production
# - as target for development (see devspace.yaml)
FROM container-registry.oracle.com/java/openjdk:21-oraclelinux8 as build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
	--add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql.rowset,jdk.management,jdk.management.agent,jdk.net,jdk.unsupported \
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

# Create project directory (workdir)
WORKDIR /app

# Install maven dependency packages
COPY pom.xml .
RUN mvn -T 1C install && rm -rf target

# Add source code files to WORKDIR
ADD . .

# Build application
RUN ./setup.sh

# Container start command for development
# Allows DevSpace to restart the dev container
# It is also possible to override this in devspace.yaml via images.*.cmd
ENTRYPOINT ["./setup.sh", "run"]

################ Production ################
# Creates a minimal image for production
FROM container-registry.oracle.com/os/oraclelinux:9 as app

ENV JAVA_HOME /usr/java/openjdk-21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=build /javaruntime $JAVA_HOME

# Continue with your application deployment
COPY --from=build /app/target/main.jar /

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENV JDK_JAVA_OPTIONS "--enable-preview"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/main.jar"]