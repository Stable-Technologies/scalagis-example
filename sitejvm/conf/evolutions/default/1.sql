# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "scalagis_testusers" ("user_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"location" geometry NOT NULL);

# --- !Downs

drop table "scalagis_testusers";

