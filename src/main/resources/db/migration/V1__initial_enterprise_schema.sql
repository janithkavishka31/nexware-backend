CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE purchase_order_status AS ENUM ('DRAFT', 'APPROVED', 'REJECTED', 'RECEIVED');
CREATE TYPE reservation_status AS ENUM ('ACTIVE', 'RELEASED', 'EXPIRED', 'FULFILLED');
CREATE TYPE stock_movement_type AS ENUM ('ADJUSTMENT', 'RESERVATION', 'RELEASE', 'TRANSFER_OUT', 'TRANSFER_IN', 'PURCHASE_RECEIPT', 'SALE');
CREATE TYPE notification_type AS ENUM ('LOW_STOCK', 'TRANSFER', 'RESERVATION', 'PURCHASE_ORDER', 'SYSTEM');
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'READ', 'FAILED');

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_roles_name CHECK (name IN (
        'ADMIN',
        'WAREHOUSE_MANAGER',
        'INVENTORY_STAFF',
        'PROCUREMENT_OFFICER',
        'SUPPLIER'
    ))
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(32),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_users_email_lowercase CHECK (email = lower(email))
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(32),
    country VARCHAR(100) NOT NULL DEFAULT 'Sri Lanka',
    capacity_units INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_warehouses_capacity_positive CHECK (capacity_units > 0)
);

CREATE TABLE warehouse_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    zone VARCHAR(50) NOT NULL,
    aisle VARCHAR(50) NOT NULL,
    rack VARCHAR(50) NOT NULL,
    bin VARCHAR(50) NOT NULL,
    capacity_units INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_warehouse_locations_slot UNIQUE (warehouse_id, zone, aisle, rack, bin),
    CONSTRAINT chk_warehouse_locations_capacity_positive CHECK (capacity_units > 0)
);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL UNIQUE,
    legal_name VARCHAR(180) NOT NULL,
    display_name VARCHAR(180) NOT NULL,
    contact_person VARCHAR(140),
    email VARCHAR(255),
    phone_number VARCHAR(32),
    address VARCHAR(500),
    tax_identifier VARCHAR(80),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE product_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id UUID REFERENCES product_categories(id) ON DELETE SET NULL,
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(140) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES product_categories(id) ON DELETE RESTRICT,
    sku VARCHAR(80) NOT NULL UNIQUE,
    barcode VARCHAR(128) UNIQUE,
    name VARCHAR(180) NOT NULL,
    description TEXT,
    brand VARCHAR(120),
    model VARCHAR(120),
    unit_cost NUMERIC(14, 2) NOT NULL DEFAULT 0,
    unit_price NUMERIC(14, 2) NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER NOT NULL DEFAULT 0,
    track_serial_numbers BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_products_cost_non_negative CHECK (unit_cost >= 0),
    CONSTRAINT chk_products_price_non_negative CHECK (unit_price >= 0),
    CONSTRAINT chk_products_low_stock_non_negative CHECK (low_stock_threshold >= 0)
);

CREATE TABLE supplier_products (
    supplier_id UUID NOT NULL REFERENCES suppliers(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    supplier_sku VARCHAR(120),
    lead_time_days INTEGER NOT NULL DEFAULT 0,
    minimum_order_quantity INTEGER NOT NULL DEFAULT 1,
    last_purchase_cost NUMERIC(14, 2),
    preferred BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (supplier_id, product_id),
    CONSTRAINT chk_supplier_products_lead_time_non_negative CHECK (lead_time_days >= 0),
    CONSTRAINT chk_supplier_products_moq_positive CHECK (minimum_order_quantity > 0)
);

CREATE TABLE product_serial_numbers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    serial_number VARCHAR(160) NOT NULL UNIQUE,
    warehouse_id UUID REFERENCES warehouses(id) ON DELETE SET NULL,
    inventory_status VARCHAR(40) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    location_id UUID REFERENCES warehouse_locations(id) ON DELETE SET NULL,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    average_unit_cost NUMERIC(14, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_inventory_warehouse_product UNIQUE (warehouse_id, product_id),
    CONSTRAINT chk_inventory_on_hand_non_negative CHECK (quantity_on_hand >= 0),
    CONSTRAINT chk_inventory_reserved_non_negative CHECK (quantity_reserved >= 0),
    CONSTRAINT chk_inventory_reserved_not_above_on_hand CHECK (quantity_reserved <= quantity_on_hand),
    CONSTRAINT chk_inventory_average_cost_non_negative CHECK (average_unit_cost >= 0)
);

CREATE TABLE inventory_reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_id UUID NOT NULL REFERENCES inventory(id) ON DELETE RESTRICT,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    reserved_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    quantity INTEGER NOT NULL,
    status reservation_status NOT NULL DEFAULT 'ACTIVE',
    reservation_reference VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    released_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_inventory_reservations_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    source_warehouse_id UUID REFERENCES warehouses(id) ON DELETE RESTRICT,
    destination_warehouse_id UUID REFERENCES warehouses(id) ON DELETE RESTRICT,
    reservation_id UUID REFERENCES inventory_reservations(id) ON DELETE SET NULL,
    movement_type stock_movement_type NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost NUMERIC(14, 2),
    reference_type VARCHAR(80),
    reference_id UUID,
    reason VARCHAR(500),
    performed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_stock_movements_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_stock_movements_unit_cost_non_negative CHECK (unit_cost IS NULL OR unit_cost >= 0)
);

CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES suppliers(id) ON DELETE RESTRICT,
    order_number VARCHAR(80) NOT NULL UNIQUE,
    status purchase_order_status NOT NULL DEFAULT 'DRAFT',
    expected_delivery_date DATE,
    approved_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    approved_at TIMESTAMPTZ,
    received_at TIMESTAMPTZ,
    rejected_reason VARCHAR(500),
    notes TEXT,
    subtotal NUMERIC(14, 2) NOT NULL DEFAULT 0,
    tax_total NUMERIC(14, 2) NOT NULL DEFAULT 0,
    grand_total NUMERIC(14, 2) NOT NULL DEFAULT 0,
    created_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_purchase_orders_totals_non_negative CHECK (
        subtotal >= 0 AND tax_total >= 0 AND grand_total >= 0
    )
);

CREATE TABLE purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    ordered_quantity INTEGER NOT NULL,
    received_quantity INTEGER NOT NULL DEFAULT 0,
    unit_cost NUMERIC(14, 2) NOT NULL,
    line_total NUMERIC(14, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_purchase_order_items_product UNIQUE (purchase_order_id, product_id),
    CONSTRAINT chk_purchase_order_items_ordered_positive CHECK (ordered_quantity > 0),
    CONSTRAINT chk_purchase_order_items_received_non_negative CHECK (received_quantity >= 0),
    CONSTRAINT chk_purchase_order_items_received_not_above_ordered CHECK (received_quantity <= ordered_quantity),
    CONSTRAINT chk_purchase_order_items_cost_non_negative CHECK (unit_cost >= 0),
    CONSTRAINT chk_purchase_order_items_line_total_non_negative CHECK (line_total >= 0)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    type notification_type NOT NULL,
    status notification_status NOT NULL DEFAULT 'PENDING',
    title VARCHAR(180) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    read_at TIMESTAMPTZ,
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_type VARCHAR(40) NOT NULL DEFAULT 'USER',
    action VARCHAR(120) NOT NULL,
    aggregate_type VARCHAR(120) NOT NULL,
    aggregate_id UUID,
    request_id VARCHAR(120),
    ip_address INET,
    user_agent VARCHAR(500),
    before_state JSONB,
    after_state JSONB,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Full platform administration'),
    ('WAREHOUSE_MANAGER', 'Warehouse operations and stock transfer management'),
    ('INVENTORY_STAFF', 'Inventory adjustments, reservations, and stock counts'),
    ('PROCUREMENT_OFFICER', 'Supplier and purchase order workflow management'),
    ('SUPPLIER', 'Supplier portal and purchase order collaboration');

CREATE INDEX idx_users_email_active ON users (email) WHERE deleted_at IS NULL;
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_warehouses_active ON warehouses (active) WHERE deleted_at IS NULL;
CREATE INDEX idx_warehouse_locations_warehouse_id ON warehouse_locations (warehouse_id);
CREATE INDEX idx_suppliers_active ON suppliers (active) WHERE deleted_at IS NULL;
CREATE INDEX idx_product_categories_parent_id ON product_categories (parent_id);
CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_name_search ON products USING gin (to_tsvector('english', coalesce(name, '') || ' ' || coalesce(description, '') || ' ' || coalesce(brand, '') || ' ' || coalesce(model, '')));
CREATE INDEX idx_supplier_products_product_id ON supplier_products (product_id);
CREATE INDEX idx_product_serial_numbers_product_id ON product_serial_numbers (product_id);
CREATE INDEX idx_inventory_product_id ON inventory (product_id);
CREATE INDEX idx_inventory_warehouse_id ON inventory (warehouse_id);
CREATE INDEX idx_inventory_low_stock_lookup ON inventory (product_id, warehouse_id, quantity_on_hand, quantity_reserved);
CREATE INDEX idx_inventory_reservations_inventory_status ON inventory_reservations (inventory_id, status);
CREATE INDEX idx_inventory_reservations_expires_at ON inventory_reservations (expires_at) WHERE status = 'ACTIVE';
CREATE INDEX idx_stock_movements_product_occurred_at ON stock_movements (product_id, occurred_at DESC);
CREATE INDEX idx_stock_movements_source_warehouse ON stock_movements (source_warehouse_id, occurred_at DESC);
CREATE INDEX idx_stock_movements_destination_warehouse ON stock_movements (destination_warehouse_id, occurred_at DESC);
CREATE INDEX idx_purchase_orders_supplier_status ON purchase_orders (supplier_id, status);
CREATE INDEX idx_purchase_orders_created_at ON purchase_orders (created_at DESC);
CREATE INDEX idx_purchase_order_items_product_id ON purchase_order_items (product_id);
CREATE INDEX idx_notifications_recipient_status ON notifications (recipient_user_id, status);
CREATE INDEX idx_notifications_payload ON notifications USING gin (payload);
CREATE INDEX idx_audit_logs_actor_created_at ON audit_logs (actor_user_id, created_at DESC);
CREATE INDEX idx_audit_logs_aggregate ON audit_logs (aggregate_type, aggregate_id);
CREATE INDEX idx_audit_logs_metadata ON audit_logs USING gin (metadata);
