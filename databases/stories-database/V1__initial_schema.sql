create table story (
    id serial not null primary key ,
    title varchar(100) ,
    detail varchar(300),
    created_at timestamp not null
)

create table story_character (
    id serial not null primary key,
    stories_id not null references stories(id),
    characters_id not null references characters(id)
)

create table story_scene (
    id serial not null primary key,
    stories_id not null references stories(id),
    scenes_id not null references scenes(id)
)

create table character (
    id serial not null primary key,
    name varchar(10) not null
)

create table scene (
    id serial not null primary key,
    description varchar(50) not null
)