create table logs
(
    id             char(36)    not null
        primary key,
    path_url       text        not null,
    header         json        not null,
    body_or_params json        not null,
    method         varchar(20) not null,
    created_at     timestamp   not null
);

