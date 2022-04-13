#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"
#include <vector>
#include "Workout.h"
#include "Trainer.h"
#include "Studio.h"
//forward declaration
class Studio;
extern Studio* backup;


enum ActionStatus{
    COMPLETED, ERROR
};

//Forward declaration
class Studio;

class BaseAction{
public:
    BaseAction();
    ActionStatus getStatus() const;
    virtual void act(Studio& studio)=0;
    virtual std::string toString() const=0;
    virtual ~BaseAction() = default;
    virtual BaseAction* clone() const=0;

protected:
    void complete();
    void error(std::string errorMsg);
    std::string getErrorMsg() const;
private:
    std::string errorMsg;
    ActionStatus status;
};


class OpenTrainer : public BaseAction {
public:
    OpenTrainer(int id, std::vector<Customer*> &customersList);
    void act(Studio &studio);
    std::string toString() const;
//    ~OpenTrainer();
 //   OpenTrainer(const OpenTrainer &other);
 //   OpenTrainer(OpenTrainer &&other);
//    const OpenTrainer & operator=(const OpenTrainer &other);
//    const OpenTrainer & operator=(OpenTrainer &&other);
    BaseAction* clone() const;
private:
	const int trainerId;
    std:: string command;
    std::vector<Customer*> customers;

};


class Order : public BaseAction {
public:
    Order(int id);
    void act(Studio &studio);
    std::string toString() const;
    ~Order() = default;
    BaseAction* clone() const;
private:
    const int trainerId;
};


class MoveCustomer : public BaseAction {
public:
    MoveCustomer(int src, int dst, int customerId);
    void act(Studio &studio);
    std::string toString() const;
    ~MoveCustomer() = default;
    BaseAction* clone() const;
private:
    const int srcTrainer;
    const int dstTrainer;
    const int id;
};


class Close : public BaseAction {
public:
    Close(int id);
    void act(Studio &studio);
    std::string toString() const;
    ~Close() = default;
    BaseAction* clone() const;
private:
    const int trainerId;
    int salary;
};


class CloseAll : public BaseAction {
public:
    CloseAll();
    void act(Studio &studio);
    std::string toString() const;
    ~CloseAll() = default;
    BaseAction* clone() const;
private:
    std::string close;
};


class PrintWorkoutOptions : public BaseAction {
public:
    PrintWorkoutOptions();
    void act(Studio &studio);
    std::string toString() const;
    ~PrintWorkoutOptions() = default;
    BaseAction* clone() const;
private:
    std::string print_workouts;
};


class PrintTrainerStatus : public BaseAction {
public:
    PrintTrainerStatus(int id);
    void act(Studio &studio);
    std::string toString() const;
    ~PrintTrainerStatus() = default;
    BaseAction* clone() const;
private:
    const int trainerId;
    std:: string status;
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
    void act(Studio &studio);
    std::string toString() const;
    ~PrintActionsLog() = default;
    BaseAction* clone() const;
private:
};


class BackupStudio : public BaseAction {
public:
    BackupStudio();
    void act(Studio &studio);
    std::string toString() const;
    ~BackupStudio() = default;
    BaseAction* clone() const;
private:
};


class RestoreStudio : public BaseAction {
public:
    RestoreStudio();
    void act(Studio &studio);
    std::string toString() const;
    ~RestoreStudio() = default;
    BaseAction* clone() const;
private:
};


#endif