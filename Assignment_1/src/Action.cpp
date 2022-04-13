#include "../include/Action.h"


//Forward declaration
class Studio;

    BaseAction:: BaseAction(): errorMsg(), status(){}
    ActionStatus BaseAction:: getStatus() const {
        return status;
    }
    void BaseAction :: complete(){
        status = COMPLETED;
    }
    void BaseAction:: error(std::string errorMsg){
        this->errorMsg = errorMsg;
        status = ERROR;
        std::cout << "Error: " <<getErrorMsg() << std::endl;
    }
    std::string BaseAction:: getErrorMsg() const{
        return errorMsg;
    }

    OpenTrainer:: OpenTrainer(int id, std::vector<Customer *> &customersList):trainerId(id) , command(), customers(customersList){};
    //create the command that was sent by the user
    void OpenTrainer:: act(Studio &studio){
        command.append("open " + std::to_string(trainerId)+ " ");
        for(Customer *c : customers)
            command.append(c->getName()+ "," + c->getType() + " ");

        if(trainerId >= studio.getNumOfTrainers() || trainerId < 0 || studio.getTrainer(trainerId)->isOpen()) {
            //delete customers of illegal trainer
            for(Customer *c : customers)
                studio.deleteUnsavedCustomer(c);
            error("Workout session does not exist or is already open.");
        }
        else {
            Trainer* t = studio.getTrainer(trainerId);
            t->openTrainer();
            if(!customers.empty()) {
                for (Customer *c: customers) {
                    if(!t->isFull()) { //if trainer's capacity isn't full
                        t->addCustomer(c);
                    } else //trainer's capacity is full
                        studio.deleteUnsavedCustomer(c);
                }
            }
            customers.clear();
            complete();
        }
    }
    std::string OpenTrainer:: toString() const{
        if(getStatus() == COMPLETED)
            return (command + "Completed");
        else
            return (command + "Error: " + getErrorMsg());
    }


    BaseAction* OpenTrainer:: clone() const {
        return new OpenTrainer(*this);
    }


    Order :: Order(int id): trainerId(id){}
    void Order :: act(Studio &studio){
        if((trainerId >= studio.getNumOfTrainers()) | (trainerId < 0) || !studio.getTrainer(trainerId)->isOpen())
            error("Trainer does not exist or is not open");
        else{
            Trainer* t = studio.getTrainer(trainerId);
            std::vector<Customer*> copy_customersList = t->getCustomers();
            for(Customer* c : copy_customersList) {
                std::string type = c->getType();
                if (!c->is_ordered()) {//the customer hasn't ordered yet
                    if (std::equal(type.begin(), type.end(), "swt"))//each customer order according its type
                        t->order(c->getId(), c->order(studio.get_cardio_workouts()), studio.getWorkoutOptions());
                    else if (std::equal(type.begin(), type.end(), "chp"))
                        t->order(c->getId(), c->order(studio.getWorkoutOptions()), studio.getWorkoutOptions());
                    else if (std::equal(type.begin(), type.end(), "mcl"))
                        t->order(c->getId(), c->order(studio.get_anaerobic_workouts()), studio.getWorkoutOptions());
                    else if (std::equal(type.begin(), type.end(), "fbd"))
                        t->order(c->getId(), c->order(studio.getWorkoutOptions()), studio.getWorkoutOptions());
                    c = nullptr;
                }
            }
            t = nullptr;
            complete();
            std:: string str;
            size_t size = copy_customersList.size();
            for( size_t i = 0 ; i < size ; i ++){
                if( i != size - 1)//avoid the last linebreak
                    str.append(copy_customersList.at(i)->toString());
                else {
                    str.append(copy_customersList.at(i)->toString());
                    str.pop_back();//remove the last linebreak
                }
            }
            std :: cout<< str << std::endl;
            copy_customersList.clear();
            str.clear();
        }
    }
    std::string Order:: toString() const{
        std:: string str;
        str.append("order " + std::to_string(trainerId) + " ");
        if(getStatus() == COMPLETED)
            str.append("Completed");
        else
            str.append("Error: " + getErrorMsg());
        return str;
    }


    BaseAction* Order:: clone() const {
        return new Order(*this);
    }




    MoveCustomer :: MoveCustomer(int src, int dst, int customerId):srcTrainer(src) , dstTrainer(dst) , id(customerId){}
    void MoveCustomer :: act(Studio &studio){
        if((srcTrainer >= studio.getNumOfTrainers()) | (srcTrainer < 0 )| (dstTrainer >= studio.getNumOfTrainers()) | (dstTrainer < 0)
        || ((!studio.getTrainer(srcTrainer)->isOpen()) | (!studio.getTrainer(dstTrainer)->isOpen())))
            error("Cannot move customer");
        else{
            Trainer* t_src = studio.getTrainer(srcTrainer);
            Trainer* t_dst = studio.getTrainer(dstTrainer);
            if(t_src->getCustomer(id) == nullptr || t_dst->isFull())
                error("Cannot move customer");
            else{
            Customer *tmp = t_src->getCustomer(id);
            t_src->removeCustomer(id);
            t_dst->addCustomer(tmp);
            tmp = nullptr;

            for( int i = 0 ; i < (int)t_src->getOrders().size() ; i++) {//move the customer's orders to the destination trainer
                std::pair<int, Workout> op = t_src->getOrders().at(i);
                if (op.first == id) {//search for the customer's orders according his ID
                    t_dst->addOrderPair(op);
                }
            }
            if(t_src->getCustomers().size()==0) {//no customers left for the source trainer
                    t_src->closeTrainer();
                }
            if(t_src->isOpen())//if the customer already ordered
                t_src->removeOrderPair(id);
            complete();
            }
            t_src = nullptr;
            t_dst = nullptr;
        }
    }
    std::string MoveCustomer :: toString() const{
        std:: string str;
        str.append("move " + std::to_string(srcTrainer) + " " + std::to_string(dstTrainer) + " " + std::to_string(id) + " ");
        if(getStatus() == COMPLETED)
            str.append("Completed");
        else
            str.append("Error: " + getErrorMsg());
        return str;
    }
    BaseAction* MoveCustomer :: clone() const {
        return new MoveCustomer(*this);
    }



    Close :: Close(int id): trainerId(id) , salary(0){}
    void Close :: act(Studio &studio){
        if((trainerId >= studio.getNumOfTrainers() )| (trainerId < 0) || (!studio.getTrainer(trainerId)->isOpen()))
            error("Trainer does not exist or is not open");
        else{
            Trainer* t = studio.getTrainer(trainerId);
            t->closeTrainer();
            salary = t->getSalary();
            complete();
            std:: cout << "Trainer " + std::to_string(trainerId) + " closed. Salary " + std::to_string(salary) +"NIS" << std:: endl;
        }
    }
    std::string Close :: toString() const{
        std:: string str;
        str.append("close " + std::to_string(trainerId) + " ");
        if(getStatus() == COMPLETED)
            str.append("Completed");
        else
            str.append("Error: " + getErrorMsg());
        return str;
    }
    BaseAction* Close :: clone() const {
        return new Close(*this);
    }




    CloseAll :: CloseAll(): close(){}
    void CloseAll :: act(Studio &studio){
        for( int i = 0 ; i < studio.getNumOfTrainers() ; i ++) {
            Trainer *t = studio.getTrainer(i);
            if (t->isOpen()) {//close only the open trainers
                t->closeTrainer();
                close.append("Trainer " + std::to_string(i) + " closed. Salary " + std::to_string(t->getSalary()) + "NIS" +'\n');
            }
            t = nullptr;
        }
        if(!close.empty())//if the string isn't empty
            std:: cout << close << std::endl;
        close.clear();
        complete();
    }
    std::string CloseAll :: toString() const{
        std:: string str;
        str.append("closeall Completed" );
        return str;
    }
    BaseAction* CloseAll :: clone() const {
        return new CloseAll(*this);
    }


    PrintWorkoutOptions :: PrintWorkoutOptions(): print_workouts(){}
    void PrintWorkoutOptions :: act(Studio &studio){
        size_t size = studio.getWorkoutOptions().size();
        for(size_t i = 0 ; i < size ; i++)
            if(i != size-1)
                print_workouts.append(studio.getWorkoutOptions().at(i).getName()+", " + studio.getWorkoutOptions().at(i).typeToString(studio.getWorkoutOptions().at(i).getType()) + ", " + std::to_string(studio.getWorkoutOptions().at(i).getPrice()) + '\n');
            else//avoid the last linebreak
                print_workouts.append(studio.getWorkoutOptions().at(i).getName()+", " + studio.getWorkoutOptions().at(i).typeToString(studio.getWorkoutOptions().at(i).getType()) + ", " + std::to_string(studio.getWorkoutOptions().at(i).getPrice()));
        complete();
        std:: cout<< print_workouts << std:: endl;
    }
    std::string PrintWorkoutOptions :: toString() const{
       return ("workout_options Completed");

    }
    BaseAction* PrintWorkoutOptions :: clone() const {
        return new PrintWorkoutOptions(*this);
    }

    PrintTrainerStatus :: PrintTrainerStatus(int id) : trainerId(id), status(){}
    void PrintTrainerStatus :: act(Studio &studio){
        if(trainerId < studio.getNumOfTrainers() && !studio.getTrainer(trainerId)->isOpen()) {
            status.append("Trainer " + std::to_string(trainerId) + " status: closed");
            complete();
            std:: cout << status << std::endl;
        }
        else if (trainerId < studio.getNumOfTrainers() && studio.getTrainer(trainerId)->isOpen()){
            status.append("Trainer " + std::to_string(trainerId) + " status: open" + '\n' + "Customers:" + '\n');
            Trainer* t = studio.getTrainer(trainerId);
            for ( Customer *c : t->getCustomers())
                status.append(std::to_string(c->getId()) + " " + c->getName() + '\n');
            std:: string s("Orders:");
            status.append(s + '\n');
            for( OrderPair op : t->getOrders())
                status.append(op.second.getName() + " " + std::to_string(op.second.getPrice()) + "NIS " + std::to_string(op.first) + '\n');
            int sal = t->getSalary() + t->cur_session_salary(); //calculate the trainers total salary since the studio was opened
            status.append("Current Trainer's Salary: " + std::to_string(sal) + "NIS");
            complete();
            std:: cout << status << std::endl;
            t = nullptr;
        }
    }
    std::string PrintTrainerStatus :: toString() const{
        return ("status " + std::to_string(trainerId) + " Completed");
    }
    BaseAction* PrintTrainerStatus :: clone() const {
        return new PrintTrainerStatus(*this);
    }

    PrintActionsLog :: PrintActionsLog(){}
    void PrintActionsLog :: act(Studio &studio) {
        std:: vector<BaseAction*> v = studio.getActionsLog();
        size_t size = v.size();
        for(size_t i = 0 ; i < size ; i++){
            BaseAction *b = v.at(i);
            std:: cout << b->toString() << std::endl;
        }
        complete();
        v.clear();
    }
    std::string PrintActionsLog :: toString() const{
        return "log Completed";
    }
    BaseAction* PrintActionsLog :: clone() const {
        return new PrintActionsLog(*this);
    }

    BackupStudio :: BackupStudio(){}
    void BackupStudio :: act(Studio &studio){
        if( backup != nullptr) { // if there is an old backup: delete backup
            delete backup;
            backup = nullptr;
        }
        backup = new Studio(studio); //create new backup according to studio
        complete();
    }
    std::string BackupStudio :: toString() const{
        return "backup Completed";
    }
    BaseAction* BackupStudio :: clone() const {
        return new BackupStudio(*this);
    }


    RestoreStudio :: RestoreStudio(){}
    void RestoreStudio :: act(Studio &studio){
        if( backup == nullptr)
            error("No backup available");
        else{
            studio = *backup; //copy backup fields to studio
            complete();
        }
    }
    std::string RestoreStudio :: toString() const{
        std:: string str;
        str.append("restore ");
        if(getStatus() == COMPLETED)
            str.append("Completed");
        else
            str.append("Error: " + getErrorMsg());
        return str;
    }
    BaseAction* RestoreStudio :: clone() const {
        return new RestoreStudio(*this);
    }





