#run in app directory
cd ..
./gradlew :app:uberJar
cd app
docker build . --tag chordpubsubnode
