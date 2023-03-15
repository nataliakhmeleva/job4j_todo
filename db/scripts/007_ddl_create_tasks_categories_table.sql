create table tasks_categories (
   id   serial primary key,
   task_id int not null REFERENCES tasks(id),
   category_id int not null REFERENCES categories(id)
);