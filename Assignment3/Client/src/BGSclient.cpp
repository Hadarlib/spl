#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <thread>
#include <iostream>
#include <mutex>
#include "../include/BGSclient.h"
#include "../include/Task.h"




int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::mutex m;
    std::condition_variable cv;
    // the task is responsible for reading the responses from the server
    Task task(connectionHandler, m, cv);
    // thread 1 reads responses from server
    std::thread t1(&Task::readFromServer, &task);
    while (1) {//this thread reads from command line
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std:: string toSend;
        // encode the string from user to the correct format to send the server
        connectionHandler.encode(line , toSend);
        if (!connectionHandler.sendLine(toSend)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if (std::equal(line.begin(), line.end(), "LOGOUT")) {
            std::unique_lock<std::mutex> lk(m);
            cv.wait(lk);
            if (task.isSuccessfulLogout())
                break;
        }
        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
    }

    t1.join();
    connectionHandler.close();
    return 0;



}








