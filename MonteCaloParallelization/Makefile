JFLAGS = -g
JC = javac
SRCDIR = src
BINDIR = bin
DATADIR = data
#Mnelisi Mabuza

# Use find command to locate all /*.java files inside the clubSimulation directory folder
CLASSES := $(shell find $(SRCDIR)/clubSimulation -name "*.java")

default: classes

classes:
	$(JC) $(JFLAGS) -cp $(BINDIR) $(CLASSES) -d $(BINDIR)

clean:
	$(RM) $(BINDIR)/ClubSimulation/*.class

run:
	java -cp $(BINDIR) clubSimulation.ClubSimulation $(ARGS)

