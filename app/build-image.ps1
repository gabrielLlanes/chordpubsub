#run in app directory
Set-Location ..
./gradlew :app:uberJar
Set-Location app
docker build . --tag chordpubsubnode