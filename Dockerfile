FROM gradle:8.5-jdk21 as builder

WORKDIR /build
COPY . .

# Build the JAR
RUN gradle build --no-daemon -x test

# Output stage
FROM scratch
COPY --from=builder /build/build/libs/VersionBridge-1.0.0.jar /
COPY --from=builder /build/build/libs/VersionBridge-1.0.0-sources.jar /
