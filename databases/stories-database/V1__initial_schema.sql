create table story (
   id         serial    not null primary key,
   title      text,
   description     text,
   characters text[],
   scene      text,
   url        text,
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