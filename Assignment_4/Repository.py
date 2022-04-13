import os
import sqlite3
import sys
import imp
import atexit
from DAO import Hats, Suppliers, Orders


class Repository:
    def __init__(self):
        self.conn = sqlite3.connect(sys.argv[4])  # create or connect to the database file
        self.hats = Hats(self.conn)  # DAO for hats table
        self.suppliers = Suppliers(self.conn)  # DAO for suppliers table
        self.orders = Orders(self.conn)  # DAO for orders table


    def close(self): 
        self.conn.commit()
        self.conn.close()

    def create_tables(self):
        self.conn.executescript("""
         CREATE TABLE hats (id INT PRIMARY KEY, topping TEXT NOT NULL, supplier INT REFERENCES suppliers(id), quantity INT NOT NULL);
         CREATE TABLE suppliers (id INT PRIMARY KEY, name TEXT NOT NULL);
         CREATE TABLE orders (id INT PRIMARY KEY, location TEXT NOT NULL, hat INT REFERENCES hats(id));         
         """)

    def update(self, topping):
        hat_info = self.hats.find_min_sup(topping)
        return hat_info


repo = Repository()  # singleton
atexit.register(repo.close)
