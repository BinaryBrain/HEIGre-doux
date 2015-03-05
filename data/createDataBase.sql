create table `aliments` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`occurrence` INTEGER DEFAULT 0 NOT NULL,`last` TIMESTAMP NOT NULL);
create unique index `name_index` on `aliments` (`name`);
create table `menus_aliments` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`id_menu` INTEGER NOT NULL,`id_aliment` INTEGER,`name` VARCHAR(254) NOT NULL,`type` INTEGER NOT NULL,`nutriment` INTEGER);
create table `menus` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`date` TIMESTAMP NOT NULL,`upvote` INTEGER NOT NULL,`downvote` INTEGER NOT NULL);
create table `nutriments_aliments` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name_F` VARCHAR(254) NOT NULL);
create table `nutriments_names` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL);
create table `nutriments_values` (`id_nutriments_aliment` INTEGER NOT NULL,`id_nutriments_name` INTEGER NOT NULL,`value` DOUBLE NOT NULL,`unit` VARCHAR(254) NOT NULL,`matrix_unit` VARCHAR(254) NOT NULL,`value_type` VARCHAR(254) NOT NULL);
create table `types` (`id` INTEGER NOT NULL PRIMARY KEY,`name` VARCHAR(254) NOT NULL);
alter table `menus_aliments` add constraint `menusaliments_aliments_fk` foreign key(`id_aliment`) references `aliments`(`id`) on update CASCADE on delete CASCADE;
alter table `menus_aliments` add constraint `menusaliments_menu_fk` foreign key(`id_menu`) references `menus`(`id`) on update CASCADE on delete CASCADE;
alter table `menus_aliments` add constraint `menusaliments_nutriments_fk` foreign key(`nutriment`) references `nutriments_aliments`(`id`) on update CASCADE on delete CASCADE;
alter table `menus_aliments` add constraint `menusaliments_type_fk` foreign key(`type`) references `types`(`id`) on update CASCADE on delete CASCADE;
alter table `nutriments_values` add constraint `nutrimentsvalues_nutrimentsaliments_fk` foreign key(`id_nutriments_aliment`) references `nutriments_aliments`(`id`) on update CASCADE on delete CASCADE;
alter table `nutriments_values` add constraint `nutrimentsvalues_nutrimentsnames_fk` foreign key(`id_nutriments_name`) references `nutriments_names`(`id`) on update CASCADE on delete CASCADE;

create fulltext index ft on aliments (name);

delimiter ^^
create procedure findFood(line varchar(255))
begin

declare word varchar(255) default "";

drop temporary table if exists tmp;
create temporary table tmp(
id integer,
name varchar(255)
);

while length(line) > 0 do

set word = substring_index(line, ' ', 1);
set line = substring(line, char_length(word) + 2, char_length(line) - char_length(word));

if strcmp(substring(word, char_length(word)), "s") = 0 then
set word = concat(substring(word, 1 , char_length(word) - 1), "*");
end if;

if char_length(word) > 3 then
insert into tmp select id, name from aliments where match (name) against (word in boolean mode) limit 1;
end if;

end while;

select * from tmp;
drop temporary table tmp;

end^^
delimiter ;

delimiter ^^
create trigger incrementAliment before insert on menus_aliments for each row
begin
	update aliments
	set occurrence = occurrence + 1,
	last = curdate()
	where id = new.id_aliment;
end;^^
delimiter ;

create view mostUpvoted as 
select id, upvote
from menus 
order by upvote desc;