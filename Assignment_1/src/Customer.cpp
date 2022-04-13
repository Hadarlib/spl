#include "../include/Customer.h"


    Customer::Customer(std::string c_name, int c_id):name(c_name),id(c_id),is_order(false){};
    std::string Customer::getName() const{
        return name;
    }
    int Customer::getId() const{
        return id;
    }
    bool Customer::is_ordered() {
        return is_order;
    }

    SweatyCustomer :: SweatyCustomer(std::string name, int id): Customer(name,id){};
    std::vector<int> SweatyCustomer :: order(const std::vector<Workout> &workout_options){
        std:: vector<int> order_list; //contains the id's of the ordered workouts by this trainer
        for( Workout w : workout_options) {// cardio workout options were sent to this customer
            str.append(getName() + " Is Doing " + w.getName() + '\n');
            order_list.push_back(w.getId());
        }
        is_order = true;
        return order_list;
    }
    std::string SweatyCustomer :: toString() const{
        return str;
    }
    std::string SweatyCustomer::getType() const {
        return "swt";
    }
    Customer* SweatyCustomer:: clone() const {
        return new SweatyCustomer(*this);
    }


    CheapCustomer :: CheapCustomer(std::string name, int id): Customer(name,id) , cheap_name(){};
    std::vector<int> CheapCustomer :: order(const std::vector<Workout> &workout_options){
        std:: vector<int> order;
        int cheapest_price = workout_options.at(0).getPrice();
        int cheapest_id = 0;
        for(Workout w : workout_options){//searching for the cheapest workout
            if(w.getPrice()<cheapest_price){
                cheapest_price = w.getPrice();
                cheapest_id = w.getId();
            }
        }
        order.push_back(cheapest_id);
        cheap_name = workout_options.at(cheapest_id).getName();
        is_order = true;
        return order;
    }
    std::string CheapCustomer :: toString() const{
        return (getName() + " Is Doing "+ cheap_name + '\n');
    }
    std::string CheapCustomer::getType() const {
         return "chp";
    }
    Customer* CheapCustomer:: clone() const {
        return new CheapCustomer(*this);
    }

    HeavyMuscleCustomer:: HeavyMuscleCustomer(std::string name, int id): Customer(name,id),str(){};
    std::vector<int> HeavyMuscleCustomer:: order(const std::vector<Workout> &workout_options) {
        std:: vector<int> order_list;
        for( Workout w : workout_options)//the anaerobic workouts options were sent to this customer
            order_list.push_back(w.getId());
        //sort the workouts ID's according their prices
        std :: sort(order_list.begin() , order_list.end() , [&workout_options](const int &a ,const int &b)->bool {
            return workout_options.at(a).getPrice()< workout_options.at(b).getPrice();});
        std::reverse(order_list.begin(), order_list.end());// this customer wants the workouts from the expensive to the cheapest
        for( int w : order_list) {
            str.append(getName() + " Is Doing " + workout_options.at(w).getName() + '\n');
        }
        is_order = true;
        return order_list;
    }
    std::string HeavyMuscleCustomer:: toString() const{
        return str;
    }
    std::string HeavyMuscleCustomer::getType() const {
        return "mcl";
    }
    Customer* HeavyMuscleCustomer:: clone() const {
        return new HeavyMuscleCustomer(*this);
    }

    FullBodyCustomer :: FullBodyCustomer(std::string name, int id): Customer(name,id),str(){};
    std::vector<int> FullBodyCustomer :: order(const std::vector<Workout> &workout_options){
        std:: vector<int> order_list;
        int cardio_cheap = -1 , anaerobic_cheap = -1 , mixed_expensive = -1;
        int cardio_cheap_price = 0 , anaerobic_cheap_price = 0 , mixed_expensive_price = 0 ;
        for (Workout w : workout_options) {
            if (w.getType() == CARDIO) {//searching the cheapest cardio workout
                if (cardio_cheap == -1) {
                    cardio_cheap = w.getId();
                    cardio_cheap_price = w.getPrice();
                } else if (w.getPrice() < cardio_cheap_price) {
                    cardio_cheap_price = w.getPrice();
                    cardio_cheap = w.getId();
                }
            }
            if (w.getType() == ANAEROBIC) {//searching the cheapest anaerobic workout
                if (anaerobic_cheap == -1) {
                    anaerobic_cheap = w.getId();
                    anaerobic_cheap_price = w.getPrice();
                } else if (w.getPrice() < anaerobic_cheap_price) {
                    anaerobic_cheap = w.getId();
                    anaerobic_cheap_price = w.getPrice();
                }
            }
            if (w.getType() == MIXED) {//searching the expensive mix-type workout
                if (mixed_expensive == -1) {
                    mixed_expensive = w.getId();
                    mixed_expensive_price = w.getPrice();
                } else if (w.getPrice() > mixed_expensive_price) {
                    mixed_expensive = w.getId();
                    mixed_expensive_price = w.getPrice();
                }
            }
        }
        order_list.push_back(cardio_cheap);
        order_list.push_back(mixed_expensive);
        order_list.push_back(anaerobic_cheap);
        str.append(getName() + " Is Doing " + workout_options.at(cardio_cheap).getName() + '\n');
        str.append(getName() + " Is Doing " +workout_options.at(mixed_expensive).getName() + '\n');
        str.append(getName() + " Is Doing " +workout_options.at(anaerobic_cheap).getName() + '\n');
        is_order = true;
        return order_list;
    }

    std::string FullBodyCustomer:: toString() const{
        return str;
    }
    std::string FullBodyCustomer::getType() const {
        return "fbd";
    }
    Customer* FullBodyCustomer:: clone() const {
        return new FullBodyCustomer(*this);
    }







