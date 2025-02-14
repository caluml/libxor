(cd ../;
./mvnw compile && \
	java -cp target/classes/ xor.cli.FileSender $1 $2 $3 $4 $5
)
