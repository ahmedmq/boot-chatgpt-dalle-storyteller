CREATE ROLE debezium WITH REPLICATION LOGIN ENCRYPTED PASSWORD 'dbz';
GRANT CREATE ON DATABASE postgres TO debezium;

create table story
(
    id         serial    not null primary key,
    title      text,
    detail     text,
    characters text[],
    scene      text,
    created_at timestamp not null
);

create table character
(
    id   serial not null primary key,
    name text   not null
);

create table scene
(
    id          serial not null primary key,
    description text   not null
);

CREATE ROLE replication_group;
GRANT replication_group TO postgres;
GRANT replication_group TO debezium;
ALTER TABLE story OWNER TO replication_group;