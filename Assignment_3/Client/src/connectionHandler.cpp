#include "../include/connectionHandler.h"



using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
            if( error == boost :: asio :: error :: eof)
                break;
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, ';');
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
            if(!getBytes(&ch,1))
                return false;
            frame.append(1, ch);
        }
        while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}
//in order to send the message in the correct format to the server
void ConnectionHandler:: encode(std:: string& msg , std:: string& encodedMsg) {
    std::string opcode;
    std::string command;
    std::size_t found = msg.find_first_of(' '); //check if msg contains a space
    std::string info;
    if (found == std::string::npos) //msg doesn't contains a space
        command = msg;
    else { //msg contains a space
        command = msg.substr(0, msg.find_first_of(' '));
        info = msg.substr(msg.find_first_of(' ') + 1);
    }
    if (command == "REGISTER") {
        opcode = "01";
        encodedMsg.append(opcode);
        addWord(info, encodedMsg);//username
        addWord(info, encodedMsg);//password
        addWord(info, encodedMsg);//birthday
    }
    if (command == "LOGIN") {
        opcode = "02";
        encodedMsg.append(opcode);
        addWord(info, encodedMsg);//username
        addWord(info, encodedMsg);//password
        addWord(info, encodedMsg);//captcha
    }
    if (command == "LOGOUT") {
        opcode = "03";
        encodedMsg.append(opcode);
    }
    if (command == "FOLLOW") {
        opcode = "04";
        encodedMsg.append(opcode);
        addWord(info, encodedMsg);//follow/unfollow
        encodedMsg.pop_back();
        addWord(info, encodedMsg);//username
    }
    if (command == "POST") {
        opcode = "05";
        encodedMsg.append(opcode);
        encodedMsg.append(info);
        char c = encodedMsg.at(encodedMsg.length()-1);
        if(c == '\n')
            encodedMsg.pop_back();
        encodedMsg.push_back('\0');
    }
    if (command == "PM") {
        opcode = "06";
        encodedMsg.append(opcode);
        addWord(info, encodedMsg);//username
        encodedMsg.append(info);
        char c = encodedMsg.at(encodedMsg.length()-1);
        if(c == '\n')
            encodedMsg.pop_back();
        encodedMsg.push_back('\0');
    }
    if (command == "LOGSTAT") {
        opcode = "07";
        encodedMsg.append(opcode);
    }
    if (command == "STAT") {
        opcode = "08";
        encodedMsg.append(opcode);
        for(size_t i = 0 ; i < info.length() ; i++){
            char c = info.at(i);
            if(c == ' ')
                encodedMsg.push_back('|');
            else if(c != '\n')
                encodedMsg.push_back(c);
        }
        encodedMsg.push_back('|'); //always finish with '|'
        encodedMsg.push_back('\0');
    }

    if (command == "BLOCK") {
        opcode = "12";
        encodedMsg.append(opcode);
        addWord(info, encodedMsg);//username
    }
}
// in order to create a msg to the client in the correct format
void ConnectionHandler:: decode(std:: string& msg, std:: string& toPrint){
    std:: string opcode = msg.substr(0,2);
    std:: string MsgOpcode = msg.substr(2,2);
    if(MsgOpcode.at(0) == '0')
        MsgOpcode = MsgOpcode.substr(1);
    if(std::equal(opcode.begin(), opcode.end(), "10")){ //ACK cases
        toPrint.append("ACK " + MsgOpcode);
        if(MsgOpcode.at(0) == '4'){//ACK for follow
            toPrint.append(" ");
            std:: string followByte = msg.substr(4,1);
            toPrint.append(followByte);
            toPrint.append(" ");
            std:: string toFollow = msg.substr(5);
            toPrint.append(toFollow);
        }
        if(MsgOpcode.at(0) == '7'){//ACK for logstat
            std:: string content = msg.substr(4);
            toPrint.append(content );
        }
        if(MsgOpcode.at(0) == '8'){//ACK for stat
            toPrint.append(msg.substr(4));
        }
    }
    else if(std::equal(opcode.begin(), opcode.end(), "11")){ //ERROR cases
        toPrint.append("ERROR " + MsgOpcode);
        if(MsgOpcode == "6" && msg.length()>4)
            toPrint.append(" " + msg.substr(4));
    }
    else if(std::equal(opcode.begin(), opcode.end(), "09")) { //NOTIFICATION cases
        toPrint.append("NOTIFICATION ");
        int index  = msg.find_first_of('\0');
        std:: string postingUser = msg.substr(4 ,index - 4);
        std:: string content = msg.substr(index + 1);
        content.pop_back();
        if(MsgOpcode.at(0) == '0') //NOTIFICATION FOR PM
            toPrint.append("PM ");
         else //NOTIFICATION FOR POST
            toPrint.append("Public ");
         toPrint.append(postingUser + " " + content);
        }
    }


void ConnectionHandler:: addWord(std:: string& info , std:: string& encodedMsg){
    std:: size_t found = info.find(" ");
    if(found != std:: string :: npos) { //not the last word
        encodedMsg.append(info.substr(0, info.find_first_of(' ')));
        int index = info.find_first_of(' ') + 1;
        info = info.substr(index);
        encodedMsg.push_back('\0');
    }
    else{ //the last word
        encodedMsg.append(info.substr(0, info.find_first_of('\n')));
        encodedMsg.push_back('\0');
    }
}
