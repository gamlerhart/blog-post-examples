FROM openjdk:8u121-jdk-alpine

COPY target/scala-2.12/useless-demo-service.jar /app/useless-demo-service.jar

# Defaults for a tiny container
ENV MEMORY="64m" \
    CPUS=1\
    ACCEPTORS=1 \
    SELECTORS=1\
    JETTY_MIN_THREADS=4 \
    JETTY_MAX_THREADS=20

EXPOSE 8080

CMD java -Xmx$MEMORY -XX:+HeapDumpOnOutOfMemoryError -XX:OnOutOfMemoryError="kill -9 %p" \
    -XX:CICompilerCount="$(($CPUS>2?$CPUS:2))" -XX:+UseSerialGC \
    -Djetty.acceptor-threads=$ACCEPTORS \
    -Djetty.selector-threads=$SELECTORS \
    -Djetty.min-threads=$JETTY_MIN_THREADS \
    -Djetty.max-threads=$JETTY_MAX_THREADS \
    -jar /app/useless-demo-service.jar
