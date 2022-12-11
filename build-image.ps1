#run in repo root directory
./gradlew :app:uberJar
docker build . --tag asmiwdtc/chordpubsubnode
docker push asmiwdtc/chordpubsubnode