-- liquibase formatted sql

-- changeset init:1752492230207-3
ALTER TABLE resources ADD resource_id INTEGER NOT NULL;

-- changeset init:1752492230207-4
ALTER TABLE resources ADD resource_type_id INTEGER NOT NULL;

-- changeset init:1752492230207-5
ALTER TABLE resources ADD CONSTRAINT UC_RESOURCESRESOURCE_ID_COL UNIQUE (resource_id);

-- changeset init:1752492230207-6
ALTER TABLE resources ADD CONSTRAINT "FK4d6jo4pc9qn6bsp24l8ii8evf" FOREIGN KEY (resource_type_id) REFERENCES resource_types (id);

-- changeset init:1752492230207-1
ALTER TABLE users DROP CONSTRAINT UC_USERSUSERNAME_COL;

-- changeset init:1752492230207-2
ALTER TABLE users ADD CONSTRAINT UC_USERSUSERNAME_COL UNIQUE (username);

