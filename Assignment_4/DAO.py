class Hats:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, hatDTO):
        self.conn.execute("""INSERT INTO hats (id, topping, supplier, quantity) VALUES (?,?,?,?)""",
                          [hatDTO.id, hatDTO.topping, hatDTO.supplier, hatDTO.quantity])

    def find_min_sup(self, topping):
        c = self.conn.cursor()
        c.execute("""SELECT id, MIN(supplier), quantity FROM hats WHERE topping = ?
        """, [topping, ])
        output = c.fetchone()
        hat_id = int(output[0])
        hat_quantity = int(output[2])-1
        if hat_quantity == 0:
            self.conn.execute('DELETE FROM hats WHERE id = ?', [hat_id])
        else:
            self.conn.execute('UPDATE hats SET quantity = ? WHERE id = ?', [hat_quantity, hat_id])

        return output


class Suppliers:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, supplierDTO):
        self.conn.execute(""" INSERT INTO suppliers (id , name) VALUES (?,?)""",
                          [supplierDTO.id, supplierDTO.name])

    def find(self, sup_id):
        c = self.conn.cursor()
        c.execute("""SELECT name FROM suppliers WHERE id = ?
                """, [sup_id, ])
        return c.fetchone()


class Orders:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, orderDTO):
        self.conn.execute(""" INSERT INTO orders (id , location , hat) VALUES (?,?,?)""",
                          [orderDTO.id, orderDTO.location , orderDTO.hat])


