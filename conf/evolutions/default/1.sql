# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

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

# --- !Downs

ALTER TABLE nutriments_values DROP FOREIGN KEY nutrimentsvalues_nutrimentsaliments_fk;
ALTER TABLE nutriments_values DROP FOREIGN KEY nutrimentsvalues_nutrimentsnames_fk;
ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_aliments_fk;
ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_menu_fk;
ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_nutriments_fk;
ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_type_fk;
drop table `types`;
drop table `nutriments_values`;
drop table `nutriments_names`;
drop table `nutriments_aliments`;
drop table `menus`;
drop table `menus_aliments`;
drop table `aliments`;

