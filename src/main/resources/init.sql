-- Initialisation de la base de données pour GestionResto

CREATE TABLE IF NOT EXISTS menu_items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    available_stock INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS restaurant_tables (
    id SERIAL PRIMARY KEY,
    table_number INT UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' -- AVAILABLE, OCCUPIED
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    table_id INT REFERENCES restaurant_tables(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PLACED', -- PLACED, COOKING, SERVED, PAID
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id),
    menu_item_id INT REFERENCES menu_items(id),
    quantity INT NOT NULL,
    price_at_time DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Données initiales
INSERT INTO restaurant_tables (table_number) VALUES (1), (2), (3), (4), (5) ON CONFLICT DO NOTHING;

INSERT INTO menu_items (name, description, price, available_stock) VALUES
('Burger Maison', 'Pain artisanal, steak haché, cheddar', 12.50, 50),
('Pizza Margherita', 'Sauce tomate, mozzarella, basilic', 10.00, 30),
('Salade César', 'Laitue, poulet, croûtons, parmesan', 9.00, 20),
('Tiramisu', 'Dessert italien classique', 6.50, 15),
('Coca-Cola', 'Boisson rafraîchissante', 3.00, 100)
ON CONFLICT DO NOTHING;
