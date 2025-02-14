(cd ../;
./mvnw compile && \
	java -cp target/classes/ xor.cli.FileXorer $1 $2 $3 $4
)
