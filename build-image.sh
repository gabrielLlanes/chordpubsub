#run in repo root directory
./gradlew :app:uberJar
docker build . --tag chordpubsubnode
