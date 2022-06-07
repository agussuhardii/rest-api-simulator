alter table rest
    change request_body_or_params body json not null;

alter table rest
    add params json null;

