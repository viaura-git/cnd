CREATE DATABASE IF NOT EXISTS mattermost CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS 'mmuser'@'%' IDENTIFIED BY 'mmuser';
GRANT ALL PRIVILEGES ON mattermost.* TO 'mmuser'@'%';
GRANT ALL PRIVILEGES ON mattermost.* TO 'viaura'@'%';
FLUSH PRIVILEGES;
