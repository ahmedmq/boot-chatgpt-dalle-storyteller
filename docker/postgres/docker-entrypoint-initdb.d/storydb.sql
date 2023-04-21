CREATE ROLE debezium WITH REPLICATION LOGIN ENCRYPTED PASSWORD 'dbz';
GRANT CREATE ON DATABASE "stories-database" TO debezium;
CREATE SCHEMA stories;
GRANT USAGE ON SCHEMA stories TO debezium ;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA stories TO debezium;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA stories TO debezium;

create table stories.story
(
    id         serial    not null primary key,
    title      text,
    detail     text,
    characters text[],
    scene      text,
    created_at timestamp not null
);

create table stories.character
(
    id   serial not null primary key,
    name text   not null
);

create table stories.scene
(
    id          serial not null primary key,
    description text   not null
);

CREATE ROLE replication_group;
GRANT replication_group TO stories;
GRANT replication_group TO debezium;
ALTER TABLE stories.story OWNER TO replication_group;