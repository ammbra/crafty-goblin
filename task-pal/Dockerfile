# Example of custom Java runtime using jlink in a multi-stage container build

FROM container-registry.oracle.com/java/openjdk:21-oraclelinux8 as jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
	--add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql.rowset,jdk.jfr,jdk.management,jdk.net,jdk.unsupported,jdk.jdwp.agent \
	--no-man-pages \
	--no-header-files \
	--output /javaruntime


# Define your base image
FROM container-registry.oracle.com/os/oraclelinux:9

ENV JAVA_HOME /usr/java/openjdk-21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=jre-build /javaruntime $JAVA_HOME

# Continue with your application deployment
COPY ./target/task-pal.jar /task-pal.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENV JDK_JAVA_OPTIONS "--enable-preview"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/task-pal.jar"]
