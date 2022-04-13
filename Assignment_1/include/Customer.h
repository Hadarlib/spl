#ifndef CUSTOMER_H_
#define CUSTOMER_H_
#include <vector>
#include <string>
#include "Workout.h"
#include <algorithm>

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Workout> &workout_options)=0;
    virtual ~Customer() = default;
    virtual std::string toString() const = 0;
    virtual std::string getType() const = 0;
    virtual Customer* clone() const=0;
    std::string getName() const;
    int getId() const;
    bool is_ordered();

private:
    const std::string name;
    const int id;

protected:
    bool is_order;
};


class SweatyCustomer : public Customer {
public:
	SweatyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    std::string getType() const;
    Customer* clone() const;
private:
   std:: string str;
};


class CheapCustomer : public Customer {
public:
	CheapCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    std::string getType() const;
    Customer* clone() const;
private:
    std:: string cheap_name;
};


class HeavyMuscleCustomer : public Customer {
public:
	HeavyMuscleCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    std::string getType() const;
    Customer* clone() const;
private:
    std:: string str;
};


class FullBodyCustomer : public Customer {
public:
	FullBodyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    std::string getType() const;
    Customer* clone() const;
private:
    std:: string str;
};


#endif