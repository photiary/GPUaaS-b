-- Migration to make edgeKey nullable
ALTER TABLE tb_container_edge ALTER COLUMN edge_key DROP NOT NULL;
