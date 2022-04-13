#ifndef STUDIO_H_
#define STUDIO_H_

#include <vector>
#include <string>
#include "Workout.h"
#include "Trainer.h"
#include "Action.h"
#include "Customer.h"
#include <fstream>
#include <sstream>
#include <istream>
#include <iostream>
#include <algorithm>
#include <utility>

//forward declaration
class Trainer;
class BaseAction;

class Studio{		
public:
	Studio();
    Studio(const std::string &configFilePath);
    Studio(const Studio &other);
    Studio(Studio &&other);
    virtual ~Studio();
    const Studio & operator=(const Studio &other);
    const Studio & operator=(Studio &&other);
    void clear();
    void getNextChar(std:: string line  , size_t &i);
    void getNextWord(std:: string &command , std:: string &s);
    void space(std:: string &command);
    void start();
    int getNumOfTrainers() const;
    Trainer* getTrainer(int tid);
	const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    std::vector<Workout>& getWorkoutOptions();
    std::vector<Workout>& get_cardio_workouts();
    std::vector<Workout>& get_anaerobic_workouts();
    Customer* newCustomer(std::string name, std::string type, int id);
    void deleteUnsavedCustomer(Customer *c);

private:
    bool open;
    int next_customer_id;
    std::vector<Trainer*> trainers;
    std::vector<Workout> workout_options;
    std::vector<BaseAction*> actionsLog;
    std::vector<Workout> cardio_workouts;
    std::vector<Workout> anaerobic_workouts;







};



#endif