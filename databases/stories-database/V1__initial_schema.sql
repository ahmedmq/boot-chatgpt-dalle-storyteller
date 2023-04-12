create table story (
    id serial not null primary key ,
    title text ,
    detail text,
    characters text[],
    scene text,
    created_at timestamp not null
)

create table character (
    id serial not null primary key,
    name text not null
)

create table scene (
    id serial not null primary key,
    description text not null
)