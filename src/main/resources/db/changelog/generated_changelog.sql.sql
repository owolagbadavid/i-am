--liquibase formatted sql

--changeset init:1.0-create-enum dbms:postgresql
--comment: Create scope_type ENUM 
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'effective_scope_rep') THEN
        CREATE TYPE effective_scope_enum AS ENUM ('DEFAULT', 'OWN', 'GROUP', 'ITEM', 'ALL');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'enforcement_scope_enum') THEN
        CREATE TYPE enforcement_scope_enum AS ENUM ('DEFAULT', 'OWN', 'GROUP');
    END IF;
END $$;

--rollback DROP TYPE effective_scope_enum; DROP TYPE enforcement_scope_enum;