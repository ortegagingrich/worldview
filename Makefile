JAVAC=javac
JAVA=java
CFLAGS=

#Setup External Dependencies
FS_DIRECTORY=$(FS)

#JME3 jars
JME3_DIRECTORY=$(FS_DIRECTORY)/dependencies/jmonkeyengine/engine/
JME3_JARS=$(shell find $(JME3_DIRECTORY) -type f -iname '*.jar')

#GDAL dependencies
GDAL_SWIG_DIRECTORY=$(FS_DIRECTORY)/dependencies/gdal-2.0.1/swig/java/
GDAL_JAR=$(GDAL_SWIG_DIRECTORY)/gdal.jar
GDAL_SOS=$(shell find $(GDAL_SWIG_DIRECTORY) -type f -iname '*.so')
GDAL_CP=$(GDAL_JAR) $(GDAL_SOS)

#classpath string for external jars
SPACE=$(null) $(null)
CLASSPATH=$(subst $(SPACE),:,$(JME3_JARS) $(GDAL_CP))

#Renderer
SRC_FOLDER=src
BIN_FOLDER=bin
MAIN_CLASS=Main

SOURCES=$(shell find $(SRC_FOLDER) -type f -iname '*.java')

#basic compilation
.phony: exe
exe: $(SOURCES)
	$(JAVAC) $(CFLAGS) -cp $(CLASSPATH) -d $(BIN_FOLDER)/ $^

#run without recompiling (make exe must be run first)
.phony: run
run:
	$(JAVA) -cp $(CLASSPATH):`pwd`/$(BIN_FOLDER)/ $(MAIN_CLASS)

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
	echo $(CLASSPATH)
