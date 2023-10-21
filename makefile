clear:
	find . -type f -name "*.class" -exec rm -f {} \;

run:
	javac -classpath . $(find ./ -type f -name '*.java') 