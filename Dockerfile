FROM eclipse-temurin:17
RUN mkdir /app
COPY app/build/libs/chordpubsubnode*.jar /app/app.jar