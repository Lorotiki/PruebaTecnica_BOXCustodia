create table users (
    id bigserial primary key,
    name varchar(100) not null,
    email varchar(150) not null unique,
    created_at timestamptz not null default now()
);

create table tasks (
    id bigserial primary key,
    title varchar(200) not null,
    description text,
    status varchar(20) not null,
    priority varchar(20) not null,
    due_date date,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    assigned_to_id bigint,
    created_by_id bigint not null,
    constraint fk_tasks_assigned_to foreign key (assigned_to_id) references users(id),
    constraint fk_tasks_created_by foreign key (created_by_id) references users(id)
);
