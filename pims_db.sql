DROP DATABASE IF EXISTS pims_db;
CREATE DATABASE pims_db;
USE pims_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    address VARCHAR(200)
);

CREATE TABLE medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATETIME NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    cashier_id INT,
    FOREIGN KEY (cashier_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);

-- Default users
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('cashier', 'cashier123', 'CASHIER');

-- Default suppliers
INSERT INTO suppliers (name, contact, address) VALUES
('MediSupply Co.', '09171234567', 'Manila'),
('HealthPlus Distributors', '09189876543', 'Cebu');

-- Default medicines
INSERT INTO medicines (name, category, price, quantity, supplier_id) VALUES
('Paracetamol 500mg', 'Painkiller', 5.50, 200, 1),
('Amoxicillin 250mg', 'Antibiotic', 12.00, 150, 1),
('Biogesic', 'Painkiller', 6.00, 300, 2),
('Neozep Forte', 'Cold', 7.25, 100, 2);