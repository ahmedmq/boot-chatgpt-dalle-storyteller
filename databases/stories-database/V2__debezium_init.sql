CREATE ROLE debezium WITH REPLICATION LOGIN ENCRYPTED PASSWORD 'dbz';
GRANT CREATE ON DATABASE "stories-database" TO debezium;
GRANT USAGE ON SCHEMA stories TO debezium ;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA stories TO debezium;
CREATE ROLE replication_group;
GRANT replication_group TO stories;
GRANT replication_group TO debezium;
ALTER TABLE stories.story OWNER TO replication_group;