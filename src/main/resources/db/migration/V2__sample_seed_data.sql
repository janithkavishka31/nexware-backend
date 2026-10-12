-- NexWare Distribution Platform — demo seed data
-- Default login for all seeded users: Password123!

-- Users (BCrypt strength 12)
INSERT INTO users (id, email, password_hash, first_name, last_name, phone_number, enabled, account_locked, version)
VALUES
    ('11111111-1111-1111-1111-111111111101'::uuid, 'admin@nexware.lk', '$2a$12$H/TuVGYPJ/3/XxVPTDB8KuejyzqqdcB6i0Wm6z/0h7eU9xBggdZeK', 'System', 'Administrator', '+94771234567', TRUE, FALSE, 0),
    ('11111111-1111-1111-1111-111111111102'::uuid, 'maya.fernando@nexware.lk', '$2a$12$H/TuVGYPJ/3/XxVPTDB8KuejyzqqdcB6i0Wm6z/0h7eU9xBggdZeK', 'Maya', 'Fernando', '+94771234568', TRUE, FALSE, 0),
    ('11111111-1111-1111-1111-111111111103'::uuid, 'kasun.perera@nexware.lk', '$2a$12$H/TuVGYPJ/3/XxVPTDB8KuejyzqqdcB6i0Wm6z/0h7eU9xBggdZeK', 'Kasun', 'Perera', '+94771234569', TRUE, FALSE, 0),
    ('11111111-1111-1111-1111-111111111104'::uuid, 'nimali.wickramasinghe@nexware.lk', '$2a$12$H/TuVGYPJ/3/XxVPTDB8KuejyzqqdcB6i0Wm6z/0h7eU9xBggdZeK', 'Nimali', 'Wickramasinghe', '+94771234570', TRUE, FALSE, 0);

INSERT INTO user_roles (user_id, role_id)
SELECT '11111111-1111-1111-1111-111111111101'::uuid, id FROM roles WHERE name = 'ADMIN'
UNION ALL
SELECT '11111111-1111-1111-1111-111111111102'::uuid, id FROM roles WHERE name = 'WAREHOUSE_MANAGER'
UNION ALL
SELECT '11111111-1111-1111-1111-111111111103'::uuid, id FROM roles WHERE name = 'INVENTORY_STAFF'
UNION ALL
SELECT '11111111-1111-1111-1111-111111111104'::uuid, id FROM roles WHERE name = 'PROCUREMENT_OFFICER';

-- Warehouses
INSERT INTO warehouses (id, code, name, address_line1, address_line2, city, state, postal_code, country, capacity_units, active, version)
VALUES
    ('22222222-2222-2222-2222-222222222201'::uuid, 'WH-COL', 'Colombo Main Distribution Center', '42 Warehouse Road', 'Peliyagoda Industrial Zone', 'Colombo', 'Western', '11300', 'Sri Lanka', 50000, TRUE, 0),
    ('22222222-2222-2222-2222-222222222202'::uuid, 'WH-KDY', 'Kandy Regional Hub', '18 Logistics Park Lane', NULL, 'Kandy', 'Central', '20000', 'Sri Lanka', 15000, TRUE, 0);

INSERT INTO warehouse_locations (id, warehouse_id, zone, aisle, rack, bin, capacity_units, active)
VALUES
    ('33333333-3333-3333-3333-333333333301'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, 'A', '01', 'R01', 'B01', 500, TRUE),
    ('33333333-3333-3333-3333-333333333302'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, 'A', '01', 'R02', 'B01', 500, TRUE),
    ('33333333-3333-3333-3333-333333333303'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, 'B', '01', 'R01', 'B01', 300, TRUE);

-- Suppliers
INSERT INTO suppliers (id, code, legal_name, display_name, contact_person, email, phone_number, address, tax_identifier, active, version)
VALUES
    ('44444444-4444-4444-4444-444444444401'::uuid, 'SUP-TECHLINE', 'TechLine Distributors Pvt Ltd', 'TechLine', 'Ruwan Silva', 'sales@techline.lk', '+94112345001', '120 Galle Road, Colombo 03', 'VAT-TL-001', TRUE, 0),
    ('44444444-4444-4444-4444-444444444402'::uuid, 'SUP-NETPRO', 'NetPro Systems Lanka Ltd', 'NetPro', 'Dilani Jayawardena', 'orders@netpro.lk', '+94112345002', '88 Duplication Road, Colombo 04', 'VAT-NP-002', TRUE, 0);

-- Product categories
INSERT INTO product_categories (id, parent_id, code, name, description, active)
VALUES
    ('55555555-5555-5555-5555-555555555501'::uuid, NULL, 'LAPTOP', 'Laptops', 'Business and gaming laptops', TRUE),
    ('55555555-5555-5555-5555-555555555502'::uuid, NULL, 'MONITOR', 'Monitors', 'Office and gaming monitors', TRUE),
    ('55555555-5555-5555-5555-555555555503'::uuid, NULL, 'NETWORK', 'Networking', 'Routers, switches, and access points', TRUE),
    ('55555555-5555-5555-5555-555555555504'::uuid, NULL, 'ACCESSORY', 'Accessories', 'Keyboards, storage, and peripherals', TRUE);

-- Products
INSERT INTO products (id, category_id, sku, barcode, name, description, brand, model, unit_cost, unit_price, low_stock_threshold, track_serial_numbers, active, version)
VALUES
    ('66666666-6666-6666-6666-666666666601'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'NX-LAP-001', '8901234567001', 'NexBook Pro 14', '14-inch business laptop', 'NexWare', 'NB-PRO-14', 850.00, 1099.00, 10, TRUE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666602'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'NX-LAP-002', '8901234567002', 'NexBook Air 13', 'Ultralight productivity laptop', 'NexWare', 'NB-AIR-13', 720.00, 949.00, 8, TRUE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666603'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'NX-MON-001', '8901234567003', 'NexView 27 4K', '27-inch 4K office monitor', 'NexView', 'NV-27-4K', 220.00, 329.00, 15, FALSE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666604'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'NX-MON-002', '8901234567004', 'NexView 24 FHD', '24-inch FHD monitor', 'NexView', 'NV-24-FHD', 140.00, 199.00, 20, FALSE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666605'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'NX-NET-001', '8901234567005', 'NexSwitch 24-Port', 'Managed gigabit switch', 'NexNet', 'NS-24G', 180.00, 259.00, 12, FALSE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666606'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'NX-NET-002', '8901234567006', 'NexRouter WiFi 6', 'Enterprise WiFi 6 router', 'NexNet', 'NR-W6', 95.00, 149.00, 25, FALSE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666607'::uuid, '55555555-5555-5555-5555-555555555504'::uuid, 'NX-KBD-001', '8901234567007', 'NexKey Mechanical', 'Mechanical keyboard', 'NexKey', 'NK-MX', 45.00, 79.00, 30, FALSE, TRUE, 0),
    ('66666666-6666-6666-6666-666666666608'::uuid, '55555555-5555-5555-5555-555555555504'::uuid, 'NX-SSD-001', '8901234567008', 'NexDrive 1TB NVMe', '1TB NVMe SSD', 'NexDrive', 'ND-1TB', 55.00, 89.00, 5, FALSE, TRUE, 0);

INSERT INTO supplier_products (supplier_id, product_id, supplier_sku, lead_time_days, minimum_order_quantity, last_purchase_cost, preferred)
VALUES
    ('44444444-4444-4444-4444-444444444401'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, 'TL-NB-PRO14', 7, 5, 820.00, TRUE),
    ('44444444-4444-4444-4444-444444444401'::uuid, '66666666-6666-6666-6666-666666666602'::uuid, 'TL-NB-AIR13', 7, 5, 700.00, TRUE),
    ('44444444-4444-4444-4444-444444444401'::uuid, '66666666-6666-6666-6666-666666666603'::uuid, 'TL-MON-27', 5, 10, 210.00, TRUE),
    ('44444444-4444-4444-4444-444444444402'::uuid, '66666666-6666-6666-6666-666666666605'::uuid, 'NP-SW-24', 10, 3, 170.00, TRUE),
    ('44444444-4444-4444-4444-444444444402'::uuid, '66666666-6666-6666-6666-666666666606'::uuid, 'NP-RT-W6', 10, 5, 90.00, TRUE);

INSERT INTO product_serial_numbers (id, product_id, serial_number, warehouse_id, inventory_status)
VALUES
    ('77777777-7777-7777-7777-777777777701'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, 'NXSN-LAP-001-0001', '22222222-2222-2222-2222-222222222201'::uuid, 'AVAILABLE'),
    ('77777777-7777-7777-7777-777777777702'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, 'NXSN-LAP-001-0002', '22222222-2222-2222-2222-222222222201'::uuid, 'AVAILABLE'),
    ('77777777-7777-7777-7777-777777777703'::uuid, '66666666-6666-6666-6666-666666666602'::uuid, 'NXSN-LAP-002-0001', '22222222-2222-2222-2222-222222222202'::uuid, 'AVAILABLE');

-- Inventory (includes one low-stock SKU at Kandy)
INSERT INTO inventory (id, warehouse_id, product_id, location_id, quantity_on_hand, quantity_reserved, average_unit_cost, version)
VALUES
    ('88888888-8888-8888-8888-888888888801'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, '33333333-3333-3333-3333-333333333301'::uuid, 45, 5, 850.00, 0),
    ('88888888-8888-8888-8888-888888888802'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '66666666-6666-6666-6666-666666666603'::uuid, '33333333-3333-3333-3333-333333333302'::uuid, 120, 0, 220.00, 0),
    ('88888888-8888-8888-8888-888888888803'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '66666666-6666-6666-6666-666666666605'::uuid, '33333333-3333-3333-3333-333333333301'::uuid, 60, 0, 180.00, 0),
    ('88888888-8888-8888-8888-888888888804'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '66666666-6666-6666-6666-666666666607'::uuid, '33333333-3333-3333-3333-333333333302'::uuid, 200, 0, 45.00, 0),
    ('88888888-8888-8888-8888-888888888805'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, '66666666-6666-6666-6666-666666666602'::uuid, '33333333-3333-3333-3333-333333333303'::uuid, 18, 0, 720.00, 0),
    ('88888888-8888-8888-8888-888888888806'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, '66666666-6666-6666-6666-666666666604'::uuid, '33333333-3333-3333-3333-333333333303'::uuid, 35, 0, 140.00, 0),
    ('88888888-8888-8888-8888-888888888807'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, '66666666-6666-6666-6666-666666666608'::uuid, '33333333-3333-3333-3333-333333333303'::uuid, 4, 0, 55.00, 0);

-- Active reservation on Colombo laptop stock
INSERT INTO inventory_reservations (id, inventory_id, product_id, warehouse_id, reserved_by_user_id, quantity, status, reservation_reference, expires_at, version)
VALUES (
    '99999999-9999-9999-9999-999999999901'::uuid,
    '88888888-8888-8888-8888-888888888801'::uuid,
    '66666666-6666-6666-6666-666666666601'::uuid,
    '22222222-2222-2222-2222-222222222201'::uuid,
    '11111111-1111-1111-1111-111111111103'::uuid,
    5,
    'ACTIVE'::reservation_status,
    'RSV-DEMO-0001',
    now() + interval '30 minutes',
    0
);

INSERT INTO stock_movements (id, product_id, source_warehouse_id, destination_warehouse_id, movement_type, quantity, unit_cost, reference_type, reason, performed_by_user_id, occurred_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, 'RESERVATION'::stock_movement_type, 5, 850.00, 'INVENTORY_RESERVATION', 'Demo reservation', '11111111-1111-1111-1111-111111111103'::uuid, now() - interval '1 hour'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa02'::uuid, '66666666-6666-6666-6666-666666666606'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, 'TRANSFER_OUT'::stock_movement_type, 25, 95.00, 'WAREHOUSE_TRANSFER', 'Regional replenishment', '11111111-1111-1111-1111-111111111102'::uuid, now() - interval '2 days'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa03'::uuid, '66666666-6666-6666-6666-666666666606'::uuid, '22222222-2222-2222-2222-222222222201'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, 'TRANSFER_IN'::stock_movement_type, 25, 95.00, 'WAREHOUSE_TRANSFER', 'Regional replenishment', '11111111-1111-1111-1111-111111111102'::uuid, now() - interval '2 days'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa04'::uuid, '66666666-6666-6666-6666-666666666608'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, '22222222-2222-2222-2222-222222222202'::uuid, 'ADJUSTMENT'::stock_movement_type, 4, 55.00, 'STOCK_ADJUSTMENT', 'Cycle count adjustment', '11111111-1111-1111-1111-111111111103'::uuid, now() - interval '3 days');

-- Purchase orders
INSERT INTO purchase_orders (id, supplier_id, order_number, status, expected_delivery_date, approved_by_user_id, approved_at, notes, subtotal, tax_total, grand_total, created_by_user_id, version)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01'::uuid, '44444444-4444-4444-4444-444444444401'::uuid, 'PO-2026-0001', 'DRAFT'::purchase_order_status, CURRENT_DATE + 14, NULL, NULL, 'Q2 laptop replenishment', 4100.00, 0.00, 4100.00, '11111111-1111-1111-1111-111111111104'::uuid, 0),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02'::uuid, '44444444-4444-4444-4444-444444444402'::uuid, 'PO-2026-0002', 'APPROVED'::purchase_order_status, CURRENT_DATE + 7, '11111111-1111-1111-1111-111111111104'::uuid, now() - interval '1 day', 'Networking restock', 1350.00, 0.00, 1350.00, '11111111-1111-1111-1111-111111111104'::uuid, 0);

INSERT INTO purchase_order_items (id, purchase_order_id, product_id, ordered_quantity, received_quantity, unit_cost, line_total)
VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccc01'::uuid, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01'::uuid, '66666666-6666-6666-6666-666666666601'::uuid, 5, 0, 820.00, 4100.00),
    ('cccccccc-cccc-cccc-cccc-cccccccccc02'::uuid, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02'::uuid, '66666666-6666-6666-6666-666666666605'::uuid, 5, 0, 170.00, 850.00),
    ('cccccccc-cccc-cccc-cccc-cccccccccc03'::uuid, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02'::uuid, '66666666-6666-6666-6666-666666666606'::uuid, 5, 0, 100.00, 500.00);

INSERT INTO notifications (id, recipient_user_id, type, status, title, message, payload)
VALUES
    ('dddddddd-dddd-dddd-dddd-dddddddddd01'::uuid, '11111111-1111-1111-1111-111111111103'::uuid, 'LOW_STOCK'::notification_type, 'PENDING'::notification_status, 'Low stock alert', 'NexDrive 1TB NVMe is below threshold at WH-KDY', '{"productSku":"NX-SSD-001","warehouseCode":"WH-KDY"}'::jsonb),
    ('dddddddd-dddd-dddd-dddd-dddddddddd02'::uuid, '11111111-1111-1111-1111-111111111102'::uuid, 'TRANSFER'::notification_type, 'SENT'::notification_status, 'Transfer completed', 'Stock transfer to Kandy regional hub completed', '{"productSku":"NX-NET-002"}'::jsonb),
    ('dddddddd-dddd-dddd-dddd-dddddddddd03'::uuid, '11111111-1111-1111-1111-111111111104'::uuid, 'PURCHASE_ORDER'::notification_type, 'PENDING'::notification_status, 'PO approved', 'PO-2026-0002 approved and ready for receiving', '{"orderNumber":"PO-2026-0002"}'::jsonb);

INSERT INTO audit_logs (id, actor_user_id, actor_type, action, aggregate_type, aggregate_id, request_id, metadata)
VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee01'::uuid, '11111111-1111-1111-1111-111111111101'::uuid, 'USER', 'POST', '/api/v1/auth/login', NULL, 'demo-seed-001', '{"note":"Seed audit sample"}'::jsonb);
