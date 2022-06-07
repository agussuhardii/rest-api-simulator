alter table logs
    modify header json null;

alter table logs
    modify body json null;

alter table logs
    modify method varchar (20) null;

alter table logs
    modify created_at timestamp null;

