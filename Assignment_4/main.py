from DTO import Hat, Supplier, Order
from Repository import repo
import os
import imp
import sys


def main(args):  # config , orders , output.txt , database
    repo.create_tables()
    with open(sys.argv[1]) as config:
        lines = config.readlines()
        line = lines[0].split(",")  # (x , y)
        number_of_hats = int(line[0])
        number_of_suppliers = int(line[1])
        for i in range(1, number_of_hats+1):
            line = lines[i]
            if line[line.__len__()-1] == '\n':
                line = line[0:line.__len__()-1]
            wordList = line.split(",")
            repo.hats.insert(Hat(*wordList))
        for i in range(number_of_hats + 1, number_of_suppliers + number_of_hats + 1):
            line = lines[i]
            if line[line.__len__() - 1] == '\n':
                line = line[0:line.__len__() - 1]
            wordList = line.split(",")
            repo.suppliers.insert(Supplier(*wordList))

    with open(sys.argv[2]) as orders:
        with open(sys.argv[3], 'w') as output_file:
            order_id = 1
            for line in orders:
                if line[line.__len__() - 1] == '\n':
                    line = line[0:line.__len__() - 1]
                wordList = line.split(",")
                hat_info = repo.update(wordList[1])
                hat_id = int(hat_info[0])
                sup_id = int(hat_info[1])
                repo.orders.insert(Order(order_id, wordList[0], hat_id))
                order_id = order_id + 1
                sup_name = repo.suppliers.find(sup_id)[0]
                output_file.write(wordList[1] + ',' + str(sup_name) + ',' + wordList[0] + '\n')


if __name__ == '__main__':
    main(sys.argv)



