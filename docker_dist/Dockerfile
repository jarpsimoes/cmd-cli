FROM registry.access.redhat.com/ubi8/openjdk-17:1.14

COPY --chown=185 target/quarkus-app/lib/ /app/lib/
COPY --chown=185 target/quarkus-app/*.jar /app/
COPY --chown=185 target/quarkus-app/app/ /app/app/
COPY --chown=185 target/quarkus-app/quarkus/ /app/quarkus/

USER 185

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
VOLUME /home/jboss/.mbio_data
