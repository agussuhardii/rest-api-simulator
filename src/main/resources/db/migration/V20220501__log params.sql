alter table logs
    change body_or_params body json not null;

alter table logs
    add params json null;

