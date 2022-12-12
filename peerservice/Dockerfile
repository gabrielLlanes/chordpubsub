FROM eclipse-temurin:17
RUN mkdir /app
COPY build/libs/peerservice*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar" ]