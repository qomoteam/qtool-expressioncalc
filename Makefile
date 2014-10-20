all:
	mvn install
	cp tool.* target/
	cd target && zip ../tool.zip tool.* *.jar
