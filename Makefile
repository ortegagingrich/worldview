JAVAC=javac
JAVA=java
CFLAGS=

FS_DIRECTORY=$(FS)
JME3_DIRECTORY=$(FS_DIRECTORY)/jmonkeyengine

SRC_FOLDER=src
BIN_FOLDER=bin
MAIN_CLASS=Main

SOURCES=$(shell find $(SRC_FOLDER) -type f -iname '*.java')

#basic compilation
.phony: exe
exe: $(SOURCES)
	$(JAVAC) $(CFLAGS) -d $(BIN_FOLDER)/ $^

#run without recompiling (make exe must be run first)
.phony: run
run:
	$(JAVA) -cp `pwd`/$(BIN_FOLDER)/ $(MAIN_CLASS)

#force compilation
.phony: .run
.run: exe run

#deletes all compiled files (from this project only, to force recompilation)
.phony: clobber
clobber:
	rm $(BIN_FOLDER)/*.class

#test
.phony: test
test:
	echo $(FS_DIRECTORY)
