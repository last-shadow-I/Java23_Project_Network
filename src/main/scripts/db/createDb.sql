-- RUN WITH postgres USER
CREATE USER network_user WITH PASSWORD 'network';
CREATE DATABASE network OWNER network_user;