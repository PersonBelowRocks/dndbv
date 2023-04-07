gradle shadowJar --console=plain
rm ./server/plugins/dndbv-1.0-SNAPSHOT.jar
cp ./build/libs/dndbv-1.0-SNAPSHOT.jar ./server/plugins