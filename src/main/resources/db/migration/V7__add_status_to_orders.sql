alter table orders
    add status varchar(20) default 'PENDING' not null after date;