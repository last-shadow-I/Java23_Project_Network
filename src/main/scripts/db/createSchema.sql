create schema if not exists "communication_lines";
create schema if not exists "hosts";
create schema if not exists "networks";

drop table if exists "communication_lines"."line_host" cascade;
drop table if exists "hosts"."host" cascade;
drop table if exists "communication_lines"."communication_line" cascade;
drop table if exists "networks"."network" cascade;

create table "networks"."network" (
	"network_id" bigserial primary key,
	"ip_address" varchar(15) not null unique,
	"mask" varchar(15) not null,
	"gateway" varchar(15) not null
);

create table "hosts"."host" (
	"id" bigserial primary key,
	"ip_address" varchar(15) not null unique,
	"mac_address" varchar(17) not null unique,
	"network_id" bigint,

	foreign key ("network_id")
		references "networks"."network"("network_id")
		on delete cascade
);

create table "communication_lines"."communication_line" (
	"id" bigserial primary key,
	"line_name" varchar(20) not null,
	"type" varchar(12) not null,
	"network_id" bigint,

	foreign key ("network_id")
		references "networks"."network"("network_id")
		on delete cascade
);

create table "communication_lines"."line_host" (
	"line_id" bigint,
	"host_id" bigint,

	foreign key ("line_id")
		references "communication_lines"."communication_line"("id")
		on delete cascade,
	foreign key ("host_id")
		references "hosts"."host"("id")
		on delete cascade,
	primary key ("line_id", "host_id")
);