
#include "../include/Trainer.h"
typedef std::pair<int, Workout> OrderPair;

    Trainer :: Trainer(int t_capacity):capacity(t_capacity), open(false), salary(0),customersList(),orderList(){}

    //Copy constructor
    Trainer:: Trainer(const Trainer &other): capacity(other.capacity), open(other.open), salary(other.salary), customersList(),orderList(){
        int cus_size = other.customersList.size();
        for( int i = 0 ; i < cus_size ; i++){
            customersList.push_back(other.customersList.at(i)->clone());
        }
        for( OrderPair op : other.orderList){
            orderList.push_back(op);
        }
    }
    //Move copy constructor
    Trainer:: Trainer(Trainer &&other): capacity(other.capacity), open(other.open), salary(other.salary), customersList(),orderList(){
        int cus_size = other.customersList.size();
        for( int i = 0 ; i < cus_size ; i++){
            customersList.push_back(other.customersList.at(i));
            other.customersList.at(i) = nullptr;
        }
        other.customersList.clear();
        for( OrderPair op : other.orderList){
            orderList.push_back(op);
        }
    }
    //Destructor
    Trainer:: ~Trainer(){
        clear();
    }
    //Assignment operator
    const Trainer& Trainer:: operator=(const Trainer &other){
        if( this != &other){
            clear();
            capacity = other.capacity;
            open = other.open;
            salary = other.salary;
            int cus_size = other.customersList.size();
            for( int i = 0 ; i < cus_size ; i++){
                customersList.push_back(other.customersList.at(i)->clone());
            }
            for( OrderPair op : other.orderList){
                orderList.push_back(op);
            }
        }
        return *this;
    }
    //Move assignment operator
    const Trainer& Trainer:: operator=(Trainer &&other){
        if( this != &other){
            clear();
            capacity = other.capacity;
            open = other.open;
            salary = other.salary;
            int cus_size = other.customersList.size();
            for( int i = 0 ; i < cus_size ; i++){
                customersList.push_back(other.customersList.at(i));
                other.customersList.at(i) = nullptr;
            }
            other.customersList.clear();
            for( OrderPair op : other.orderList){
                orderList.push_back(op);
            }
            other.orderList.clear();
        }
        return *this;
    }

    void Trainer:: clear(){
        for( Customer *c : customersList){
            if(c)
                delete c;
        }
        customersList.clear();
        orderList.clear();
    }

    int Trainer :: getCapacity() const{
        return capacity;
    }

    void Trainer :: addCustomer(Customer* customer){
            customersList.push_back(customer);
    }

    void Trainer :: removeCustomer(int id){
        std:: vector<Customer*> tmp;
        int size = customersList.size();
        for( int i = 0 ; i < size; i++) { //copy all the customers that different from the one to be removed
            if(customersList.at(i)->getId() != id)
                tmp.push_back(customersList.at(i));
            else
                customersList.at(i) = nullptr; //the one to be removed
        }
        customersList.clear();
        for( Customer* c : tmp){ //copy the updated customers list to the field customersList
            customersList.push_back(c);
            c = nullptr;
        }
        tmp.clear();
    }
    Customer* Trainer :: getCustomer(int id){
        size_t i = 0;
        while( i< customersList.size() ){
            if(customersList.at(i)->getId() == id)
                return customersList.at(i);
            i++;
        }
        return nullptr;
    }

    std::vector<Customer*>& Trainer :: getCustomers(){
        return customersList;
    }

    std::vector<OrderPair>& Trainer :: getOrders(){
        return orderList;
    }

    void Trainer :: order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options){
        for(int i : workout_ids) { //loop over the requested workouts for the customer and create the orderPair
            std::pair<int, Workout> op(customer_id, workout_options.at(i));
            addOrderPair(op);
        }
    }

    void Trainer :: openTrainer(){
        open = true;
    }

    void Trainer :: closeTrainer(){
        open = false;
        salary += cur_session_salary();
       for(Customer *c : customersList){
           delete c;
        }
        customersList.clear();
        orderList.clear();
    }

    int Trainer :: getSalary(){
        return salary;
    }

    bool Trainer :: isOpen() {
        return open;
    }

    void Trainer :: addOrderPair(std::pair<int, Workout> op){
        orderList.push_back(op);
    }

    void Trainer :: removeOrderPair(int i) {
        std:: vector<OrderPair> tmp;
        int size = getOrders().size();
        for( int j = 0 ; j < size; j++) { //copy all the orderPairs that different from the one to be removed
            if (getOrders().at(j).first != i) {
                std::pair<int, Workout> op = getOrders().at(j);
                tmp.push_back(op);
            }
        }
       orderList.clear();
        for( OrderPair op : tmp){ //copy the updated OrderPairs list to the field OrderPair
            orderList.push_back(op);
        }
       tmp.clear();
    }

    bool Trainer :: isFull() {
         return getCustomers().size() == (size_t)getCapacity();
    }
    //when a trainer's session is closed: calculate his current salary according to the workouts in the session
    int Trainer :: cur_session_salary(){
        int cur = 0;
        for(std::pair<int, Workout> op : orderList)
            cur += op.second.getPrice();
        return cur;
    }


