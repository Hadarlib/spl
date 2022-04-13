#ifndef BGSCLIENT_TASK_H
#define BGSCLIENT_TASK_H
#include "connectionHandler.h"
#include <thread>
#include <iostream>
#include <mutex>
#include <condition_variable>

class Task{
private:
    ConnectionHandler &handler;
    std:: mutex &mutex;
    std:: condition_variable &cv;
    bool logout;


public:
    Task(ConnectionHandler &c_handler, std:: mutex &_mutex , std:: condition_variable &_cv);
    bool isSuccessfulLogout();
    void readFromServer();

};





















#endif //BGSCLIENT_TASK_H
