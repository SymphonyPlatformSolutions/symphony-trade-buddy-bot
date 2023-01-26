FROM amazoncorretto:17
RUN jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql.rowset,jdk.attach,jdk.httpserver,jdk.jdi,jdk.jfr,jdk.management,jdk.net,jdk.unsupported,jdk.crypto.ec \
    --strip-java-debug-attributes \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre

FROM debian:bullseye-slim
COPY --from=0 /jre /jre
WORKDIR /data/symphony
COPY workflows workflows
COPY workflow-bot-app.jar app.jar
COPY application.yaml application.yaml
ENTRYPOINT [ \
  "/bin/sh", "-c", \
  "sed -i \"s/{{IEX_TOKEN}}/$TOKEN/g\" workflows/*.swadl.yaml && /jre/bin/java -jar app.jar --spring.profiles.active=prod" \
]
