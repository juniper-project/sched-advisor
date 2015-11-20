DROP ALIAS seconds;
ALTER TABLE metrics DROP CONSTRAINT record_has_recorded_metrics;
DROP TABLE metrics IF EXISTS;
DROP TABLE records IF EXISTS;

