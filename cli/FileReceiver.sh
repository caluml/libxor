(cd ../;
./mvnw compile && \
	java -cp target/classes/ xor.cli.FileReceiver $1 $2 $3 $4
)
