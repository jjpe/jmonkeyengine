#
# Create evolizer database and user.
#
# Command: mysql -u root -p < create_database.sql
#

CREATE DATABASE evolizer CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE USER 'evolizer' IDENTIFIED BY 'evolizer';
GRANT ALL ON evolizer.* TO 'evolizer'@'%' IDENTIFIED BY 'evolizer';
GRANT ALL ON evolizer.* TO 'evolizer'@'localhost' IDENTIFIED BY 'evolizer';
FLUSH PRIVILEGES;
