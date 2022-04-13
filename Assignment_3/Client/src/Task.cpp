#include "../include/Task.h"

Task::Task(ConnectionHandler &c_handler, std:: mutex &_mutex, std:: condition_variable &_cv) :handler(c_handler), mutex(_mutex), cv(_cv) , logout(false){};
bool Task ::isSuccessfulLogout() {
    return logout;
}
void Task::readFromServer() {
    while(1) {
        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!handler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std:: string toPrint;
        // decode the string to be at the correct format to the client to read
        handler.decode(answer , toPrint);
        int len = toPrint.length();
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        if(toPrint.at(len -1) == '\n')
            toPrint.resize(len - 1);
        if(toPrint.at(len -1) == ';')
            toPrint.resize(len - 1);
        std::cout <<  toPrint  << std::endl ;
        if (toPrint == "ACK 3") {//successful logout
            std::cout << "Exiting...\n" << std::endl;
            // the client successfully logged out
            logout = true;
            cv.notify_one();
            break;
        }
        else if (toPrint == "ERROR 3"){
            // the client hasn't successfully logged out , needs to try again
            cv.notify_one();
        }
    }
}
