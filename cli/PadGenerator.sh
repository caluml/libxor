(cd ../; 
./mvnw compile && \
	java -cp target/classes/ xor.cli.PadGenerator $1 $2
)
