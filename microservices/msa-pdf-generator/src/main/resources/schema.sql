CREATE TABLE IF NOT EXISTS movements (
    movement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_key VARCHAR(255) NOT NULL,
    account_id BIGINT NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('DEBIT', 'CREDIT')),
    amount DECIMAL(19,2) NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    description VARCHAR(500),
    reference VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de prueba
INSERT INTO movements (unique_key, account_id, occurred_at, movement_type, amount, balance, description, reference) VALUES
('TXN001', 12345, '2024-01-15 10:30:00', 'CREDIT', 1000.00, 1000.00, 'Depósito inicial', 'REF001'),
('TXN002', 12345, '2024-01-16 14:20:00', 'DEBIT', 150.50, 849.50, 'Compra en supermercado', 'REF002'),
('TXN003', 12345, '2024-01-17 09:15:00', 'CREDIT', 500.00, 1349.50, 'Transferencia recibida', 'REF003'),
('TXN004', 12345, '2024-01-18 16:45:00', 'DEBIT', 75.25, 1274.25, 'Pago de servicios', 'REF004'),
('TXN005', 67890, '2024-01-15 11:00:00', 'CREDIT', 2000.00, 2000.00, 'Depósito inicial', 'REF005'),
('TXN006', 67890, '2024-01-16 15:30:00', 'DEBIT', 300.00, 1700.00, 'Retiro ATM', 'REF006');
