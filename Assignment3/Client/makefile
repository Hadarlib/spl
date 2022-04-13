CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/BGSclient.o bin/connectionHandler.o bin/echoClient.o bin/Task.o  $(LDFLAGS)

BGSclient: bin/BGSclient.o bin/connectionHandler.o bin/echoClient.o bin/Task.o
	@echo 'Building target: main'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGSclient bin/connectionHandler.o bin/Task.o bin/BGSclient.o $(LDFLAGS)
	@echo 'Finished building target: main'
	@echo ''

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/Task.o: src/Task.cpp
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp

.PHONY: clean
clean:
	rm -f bin/*
