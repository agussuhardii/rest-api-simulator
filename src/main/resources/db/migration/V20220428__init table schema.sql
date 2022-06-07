create table rest
(
    id                      char(36)     not null primary key,
    name                    varchar(100) not null,

    path_url                text         not null,
    method                  varchar(10)  not null,
    request_body_or_params  json,

    success_response_body   json         not null,
    fail_response_body      json         not null,

    success_response_code   smallint     not null,
    fail_response_code      smallint     not null,

    success_response_header json         not null,
    fail_response_header    json         not null,

    request_headers         json         not null,

    response_in_nano_second bigint       not null default 0
);

