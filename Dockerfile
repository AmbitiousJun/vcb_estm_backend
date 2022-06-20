FROM maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
WORKDIR /app
COPY vcb_estm_backend-1.0-SNAPSHOT.jar .

# Build a release artifact.
# RUN mvn package -DskipTests

# Run the web service on container startup.
# CMD ["java","-jar","/app/target/vcb_estm_backend-1.0-SNAPSHOT.jar","--spring.profiles.active=test"]
CMD ["java","-jar","/app/vcb_estm_backend-1.0-SNAPSHOT.jar","--spring.profiles.active=test"]