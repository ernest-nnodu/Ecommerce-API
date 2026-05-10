alter table customers
    add role varchar(20) default 'USER' not null;