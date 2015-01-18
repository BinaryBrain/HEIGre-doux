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