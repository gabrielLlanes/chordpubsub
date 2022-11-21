rm -r ./build
../gradlew :peerservice:build
docker build . --tag asmiwdtc/peerservice
docker push asmiwdtc/peerservice