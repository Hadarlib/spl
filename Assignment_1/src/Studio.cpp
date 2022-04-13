#include "../include/Studio.h"

using namespace std;


    Studio::Studio(): open(false) ,  next_customer_id(0), trainers(), workout_options(), actionsLog(), cardio_workouts(),anaerobic_workouts(){}
    Studio::Studio(const std::string &configFilePath) : open(false) , next_customer_id(0), trainers(), workout_options(), actionsLog() ,cardio_workouts(),anaerobic_workouts() {
        size_t numOfTrainers;
        std::ifstream myFile;
        myFile.open(configFilePath);
        std::string line;

        while (myFile) {
            getline(myFile, line);
            std::istringstream ss(line);
            std::string word;
            if (!line.empty()) { //build the studio by the input
                if ((line.at(0) == '#') & (line.at(1) == 'N' || line.at(2) == 'N')) {
                    getline(myFile, line);
                    numOfTrainers = line.at(0) - '0';
                } 
                else if ((line.at(0) == '#') & (line.at(1) == 'T' || line.at(2) == 'T')) {
                    getline(myFile, line);
                    size_t i = 0;
                    getNextChar(line, i);
                    line = line.substr(i);
                    while (trainers.size() < numOfTrainers) {//creat new trainers with specific capacity
                        //if (line.at(i) != ',' & line.at(i) != ' ') {
                        string capi;
                        getNextWord(line, capi);
                        int cap = stoi(capi, nullptr, 10);
                        Trainer *t = new Trainer(cap);
                        trainers.push_back(t);
                    }
                } 
                else if ((line.at(0) == '#') & (line.at(1) == 'W' || line.at(2) == 'W')) {
                    getline(myFile, line);
                    int id = 0;
                    while (myFile) {//as long as there is commands to read from input
                        size_t i = 0;
                        std::string w_name("");//workout's name
                        WorkoutType w_type; // workout's type
                        std::string price("");
                        int w_price; // workout's price
                        for (i = 0 ; i < line.size() ; i++) {
                            if (line.at(i) != ',')
                                w_name += line.at(i);
                            else {
                                break;
                            }
                        }//prepare command for ther next word to read
                        line = line.substr(w_name.size());
                        i = 0;
                        getNextChar(line, i);
                        line = line.substr(i);
                        i = 0;
                        int size;
                        if (line.at(i) == 'A') {
                            w_type = ANAEROBIC;
                            size = 9;
                        } else if (line[i] == 'M') {
                            w_type = MIXED;
                            size = 5;
                        } else {
                            w_type = CARDIO;
                            size = 6;
                        }
                        line = line.substr(size);
                        i = 0;
                        getNextChar(line, i);
                        line = line.substr(i);
                        getNextWord(line, price);
                        w_price = stoi(price, nullptr, 10);
                        //create new workout option
                        workout_options.push_back(Workout(id, w_name, w_price, w_type));
                        if (w_type == CARDIO) // create vector of only cardio workouts as received in the input
                            cardio_workouts.push_back(Workout(id, w_name, w_price, w_type));
                        else if (w_type == ANAEROBIC) // create vector of only anaerobic workouts that will be sorted by price
                            anaerobic_workouts.push_back(Workout(id, w_name, w_price, w_type));
                        getline(myFile, line);
                        if (line.empty())
                            break;
                        id++;
                    }
                }
            }
        }
    }

    //Copy Constructor
    Studio::Studio(const Studio &other):open(other.open), next_customer_id(other.next_customer_id), trainers(),
        workout_options(), actionsLog(),cardio_workouts(), anaerobic_workouts() {
        size_t t_size = other.trainers.size();
        for( size_t i = 0 ; i < t_size ; i++){
            trainers.push_back(new Trainer(*other.trainers.at(i)));//call for copy constructor of trainer
        }
        size_t action_size = other.actionsLog.size();
        for( size_t i = 0 ; i < action_size ; i++){
            actionsLog.push_back(other.actionsLog.at(i)->clone());
        }
        for( Workout op : other.workout_options){
            workout_options.push_back(op);
        }
        for( Workout op : other.cardio_workouts){
            cardio_workouts.push_back(op);
        }
        for( Workout op : other.anaerobic_workouts){
            anaerobic_workouts.push_back(op);
        }
    }
    //Move copy Constructor
    Studio::Studio(Studio &&other): open(other.open), next_customer_id(other.next_customer_id),trainers(), workout_options(), actionsLog(),cardio_workouts(), anaerobic_workouts(){
        size_t t_size = other.trainers.size();
        for( size_t i = 0 ; i < t_size ; i++){
            trainers.push_back(other.trainers.at(i));
            other.trainers.at(i) = nullptr;
        }
        other.trainers.clear(); 
        size_t action_size = other.actionsLog.size();
        for( size_t i = 0 ; i < action_size ; i++){
            actionsLog.push_back(other.actionsLog.at(i));
            other.actionsLog.at(i) = nullptr;
        }
        other.actionsLog.clear(); 
        for( Workout op : other.workout_options){
            workout_options.push_back(op);
        }
        other.workout_options.clear();
        for( Workout op : other.cardio_workouts){
            cardio_workouts.push_back(op);
        }
        other.cardio_workouts.clear();
        for( Workout op : other.anaerobic_workouts){
            anaerobic_workouts.push_back(op);
        }
        other.anaerobic_workouts.clear();
    }
    //Destructor
    Studio:: ~Studio(){
        clear();
    }
    //Assignment operator =
    const Studio& Studio:: operator=(const Studio &other){
        if( this != &other){
            clear();
            open = other.open;
            next_customer_id = other.next_customer_id;

            size_t t_size = other.trainers.size();
            for( size_t i = 0 ; i < t_size ; i++){
                trainers.push_back(new Trainer(*other.trainers.at(i)));
            }
            size_t action_size = other.actionsLog.size();
            for( size_t i = 0 ; i < action_size ; i++){
                actionsLog.push_back(other.actionsLog.at(i)->clone());
            }
            for( Workout op : other.workout_options){
                workout_options.push_back(op);
            }
            for( Workout op : other.cardio_workouts){
                cardio_workouts.push_back(op);
            }
            for( Workout op : other.anaerobic_workouts){
                anaerobic_workouts.push_back(op);
            }
        }
        return *this;
    }
    //Move assignment operator =
    const Studio& Studio:: operator=(Studio &&other){
        if( this != &other){
            clear();
            open = other.open;
            next_customer_id = other.next_customer_id;

            size_t t_size = other.trainers.size();
            for( size_t i = 0 ; i < t_size ; i++){
                trainers.push_back(other.trainers.at(i));
                other.trainers.at(i) = nullptr;
            }
            other.trainers.clear();
            size_t action_size = other.actionsLog.size();
            for( size_t i = 0 ; i < action_size ; i++){
                actionsLog.push_back(other.actionsLog.at(i));
                other.actionsLog.at(i) = nullptr;
            }
            other.actionsLog.clear();
            for( Workout op : other.workout_options){
                workout_options.push_back(op);
            }
            other.workout_options.clear();
            for( Workout op : other.cardio_workouts){
                cardio_workouts.push_back(op);
            }
            other.cardio_workouts.clear();
            for( Workout op : other.anaerobic_workouts){
                anaerobic_workouts.push_back(op);
            }
            other.anaerobic_workouts.clear();
        }
        return *this;
    }
    void Studio:: clear(){
        workout_options.clear();
        cardio_workouts.clear();
        anaerobic_workouts.clear();
        for( Trainer *t : trainers){
            if(t)
                delete t;
        }
        trainers.clear();
        for( BaseAction *b : actionsLog){
            if(b)
                delete b;
        }
        actionsLog.clear();
    }
    // find the index of the next char in command
    void Studio:: getNextChar(std:: string line  , size_t &i){
        while(sizeof(line) > 0 && i < sizeof(line) && ((line.at(i) == ' ' )| (line.at(i) == ',')))
            i++;
    }
    // create new word from command
    void Studio:: getNextWord(std:: string &command  , string &s) {
        size_t i ;
        for (i = 0; i < command.size(); i++) {
            if ((command.at(i) != ',') & (command.at(i) != ' '))
                s += command.at(i);
            else {
                break;
            }
        }
        if (command.size() > s.size()) { //if there is more command words to check: prepare the command for the next word
            command = command.substr(s.size());
            i = 0;
            getNextChar(command, i);
            command = command.substr(i);
            i = 0;
        }
        else if (command.size() == s.size()) // if the command word is the last word in command
            command.clear();
    }
        // delete spaces from the end of the command line 
        void Studio :: space(std:: string &command){
            int j = command.size()-1 , i;
            for( i = command.size()-1 ; i >= 0 ; i--){
            if(command.at(i) != ' ')
                break;
            }
            if( i != j) //if there are spaces: delete
                command = command.substr(0 , i+1);
        }

        void Studio:: start(){
            std:: cout << "Studio is now open!" <<endl;
            open = true;
            while(open){
                string command;
                getline(std:: cin,command);
                space(command);
                string act; // act to preform from command line
                getNextWord(command , act);
                if(std::equal(act.begin(), act.end(), "open")) {
                    string trainer_id_s;
                    int trainer_id;
                    getNextWord(command,trainer_id_s);
                    trainer_id = stoi(trainer_id_s);
                    std::vector<Customer *> customersList;
                    for (int i = 0; !command.empty() ; i++) { //creates the trainer's customers list
                        string c_name; //customer name
                        string c_type; //customer's type 
                        getNextWord(command, c_name);
                        getNextWord(command, c_type);

                        Customer *c = newCustomer(c_name, c_type, next_customer_id);
                        next_customer_id++; //increases by 1 for every new customer
                        customersList.push_back(c);
                        c = nullptr;
                    }
                        OpenTrainer* op = new OpenTrainer(trainer_id, customersList);
                        op->act(*this);
                        actionsLog.push_back(op);
                        op = nullptr;//#
                customersList.clear();
                }
            else if(std::equal(act.begin(), act.end(), "order")) {
                string trainer_id_s;
                int trainer_id;
                getNextWord(command,trainer_id_s);
                trainer_id = stoi(trainer_id_s);
                Order* ordi = new Order(trainer_id);
                ordi->act(*this);
                actionsLog.push_back(ordi);
                ordi = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "move")) {
                string src_s , dst_s , cus_id_s;
                int src , dst , cus_id;
                getNextWord(command,src_s);
                getNextWord(command,dst_s);
                getNextWord(command,cus_id_s);
                src = stoi(src_s);
                dst = stoi(dst_s);
                cus_id = stoi(cus_id_s);
                MoveCustomer* move = new MoveCustomer(src, dst, cus_id);
                move->act(*this);
                actionsLog.push_back(move);
                move = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "close")) {
                string trainer_id_s;
                int trainer_id;
                getNextWord(command,trainer_id_s);
                trainer_id = stoi(trainer_id_s);
                Close* cl = new Close(trainer_id);
                cl->act(*this);
                actionsLog.push_back(cl);
                cl = nullptr; 
            }
            else if(std::equal(act.begin(), act.end(), "closeall")) {
                CloseAll* cl_a = new CloseAll();
                cl_a->act(*this);
                actionsLog.push_back(cl_a);
                open = false;
                cl_a = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "workout_options")) {
                PrintWorkoutOptions* print_wo = new PrintWorkoutOptions();
                print_wo->act(*this);
                actionsLog.push_back(print_wo);
                print_wo = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "status")) {
                string trainer_id_s;
                int trainer_id;
                getNextWord(command,trainer_id_s);
                trainer_id = stoi(trainer_id_s);
                PrintTrainerStatus* print_ts = new PrintTrainerStatus(trainer_id);
                print_ts->act(*this);
                actionsLog.push_back(print_ts);
                print_ts = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "log")) {
                PrintActionsLog* print_ac = new PrintActionsLog();
                print_ac->act(*this);
                actionsLog.push_back(print_ac);
                print_ac = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "backup")) {
                BackupStudio* back = new BackupStudio();
                back->act(*this);
                actionsLog.push_back(back);
                back = nullptr;
            }
            else if(std::equal(act.begin(), act.end(), "restore")) {
                RestoreStudio* restore = new RestoreStudio();
                restore->act(*this);
                actionsLog.push_back(restore);
                restore = nullptr; 
            }
        }
    }
    
    int Studio:: getNumOfTrainers() const{
        return trainers.size();
    }
    Trainer* Studio:: getTrainer(int tid){
        return trainers.at(tid);
    }
    // Return a reference to the history of actions
    const std::vector<BaseAction*>& Studio:: getActionsLog() const{
        return actionsLog;
    } 

    std::vector<Workout>& Studio:: getWorkoutOptions(){
        return workout_options;
    }

    std::vector<Workout>& Studio:: get_cardio_workouts(){
        return cardio_workouts;
    }

    std::vector<Workout>& Studio:: get_anaerobic_workouts(){
        return anaerobic_workouts;
    }
    // return a pointer to new customer that was created by the builder of the customer type
    Customer* Studio:: newCustomer(std::string name, std::string type, int id){
        if(std::equal(type.begin(), type.end(), "swt")) {
            return new SweatyCustomer(name , id);
        }
        else if(std::equal(type.begin(), type.end(), "chp")) {
            return new CheapCustomer(name , id);
        }
        else if(std::equal(type.begin(), type.end(), "mcl")) {
            return new HeavyMuscleCustomer(name , id);
        }
        else if(std::equal(type.begin(), type.end(), "fbd")) {
            return new FullBodyCustomer(name , id);
        }
        return nullptr;
    }
    // delete customers that were created but never used because of trainers capacity\trainer is open\trainer doesn't exists
    void Studio::deleteUnsavedCustomer(Customer *c) {
        delete c;
        next_customer_id--;
    }











