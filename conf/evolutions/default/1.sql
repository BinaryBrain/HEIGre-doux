# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `aliments` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`type` INTEGER NOT NULL);
create table `menus_aliments` (`id_menu` INTEGER NOT NULL,`id_aliment` INTEGER NOT NULL);
create table `menus` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`date` TIMESTAMP NOT NULL);
create table `types` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL);
alter table `aliments` add constraint `aliments_type_fk` foreign key(`type`) references `types`(`id`) on update CASCADE on delete RESTRICT;
alter table `menus_aliments` add constraint `menusaliments_aliments_fk` foreign key(`id_menu`) references `aliments`(`id`) on update CASCADE on delete RESTRICT;
alter table `menus_aliments` add constraint `menusaliments_menu_fk` foreign key(`id_menu`) references `menus`(`id`) on update CASCADE on delete RESTRICT;

# --- !Downs

ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_aliments_fk;
ALTER TABLE menus_aliments DROP FOREIGN KEY menusaliments_menu_fk;
ALTER TABLE aliments DROP FOREIGN KEY aliments_type_fk;
drop table `types`;
drop table `menus`;
drop table `menus_aliments`;
drop table `aliments`;

