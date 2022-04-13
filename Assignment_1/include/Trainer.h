#ifndef TRAINER_H_
#define TRAINER_H_

#include <vector>
#include "Customer.h"
#include "Workout.h"
#include <iostream>

typedef std::pair<int, Workout> OrderPair;

class Trainer{
public:
    Trainer(int t_capacity);
    Trainer(const Trainer &other);
    Trainer(Trainer &&other);
    virtual ~Trainer();
    const Trainer & operator=(const Trainer &other);
    const Trainer & operator=(Trainer &&other);
    void clear();
    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*> & getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options);
    void openTrainer();
    void closeTrainer();
    int getSalary();
    bool isOpen();
    void addOrderPair(std::pair<int, Workout> op);
    void removeOrderPair(int i);
    bool isFull();
    int cur_session_salary();

private:
    int capacity;
    bool open;
    int salary;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order for the trainer - (customer_id, Workout)

};


#endif