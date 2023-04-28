CREATE DATABASE crm_apps;

USE crm_apps;

CREATE TABLE roles(
    id INT AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE,
    description TEXT,

    PRIMARY KEY (id)
);

CREATE TABLE users(
    id INT AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    fullname VARCHAR(100),
    avatar TEXT,
    role_id INT,
    
    PRIMARY KEY (id),
    CONSTRAINT FK_users_role_id FOREIGN KEY(role_id) REFERENCES roles(id)
);

CREATE TABLE statuses(
    id INT AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE,

    PRIMARY KEY (id)
);

CREATE TABLE projects(
    id INT AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE,
    start_date DATE,
    end_date DATE,
    leader_id INT,
    
    PRIMARY KEY (id),
    CONSTRAINT FK_projects_leader_id FOREIGN KEY(leader_id) REFERENCES users(id)
);

CREATE TABLE tasks(
    id INT AUTO_INCREMENT,
    name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    user_id INT,
    project_id INT,
    status_id INT,
    
    PRIMARY KEY (id),
    CONSTRAINT FK_tasks_user_id FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT FK_tasks_project_id FOREIGN KEY(project_id) REFERENCES projects(id),
    CONSTRAINT FK_tasks_status_id FOREIGN KEY(status_id) REFERENCES statuses(id)
);

INSERT INTO statuses(name) VALUE ("Chưa bắt đầu");
INSERT INTO statuses(name) VALUE ("Đang thực hiện");
INSERT INTO statuses(name) VALUE ("Đã hoàn thành");

INSERT INTO roles(name,description) VALUE ("ADMIN","Quản trị hệ thống");
INSERT INTO roles(name,description) VALUE ("LEADER","Quản lý");
INSERT INTO roles(name,description) VALUE ("STAFF","Nhân viên");

INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("tys1@gmail.com","1111","Nguyễn Tý","tys.jpg",3);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("suu2@gmail.com","2222","Lê Sửu","suu.jpg",2);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("dan3@gmail.com","3333","Trần Dần","dan.jpg",3);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("meo4@gmail.com","4444","Hoàng Mẹo","meo.jpg",1);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("thin5@gmail.com","5555","Võ Thìn","thin.jpg",3);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("tyj6@gmail.com","6666","Đỗ Tỵ","tyj.jpg",3);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("ngo7@gmail.com","7777","Trương Ngọ","ngo.jpg",2);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("mui8@gmail.com","8888","Bùi Mùi","mui.jpg",3);
INSERT INTO users(email,password,fullname,avatar,role_id) VALUE ("than9@gmail.com","9999","Huỳnh Thân","than.jpg",3);

INSERT INTO projects(name,start_date,end_date,leader_id) VALUE ("Dự án Storm","2023-01-01","2023-03-31",2);
INSERT INTO projects(name,start_date,end_date,leader_id) VALUE ("Dự án Wind","2023-04-01","2023-06-30",7);
INSERT INTO projects(name,start_date,end_date,leader_id) VALUE ("Dự án Zues","2023-05-01","2023-07-31",2);
INSERT INTO projects(name,start_date,end_date,leader_id) VALUE ("Dự án Hades","2023-08-01","2023-10-31",7);

INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Quản lý dự án Storm","2023-01-01","2023-03-31",2,1,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Phân tích dự án","2023-01-01","2023-01-15",2,1,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Thiết kế database","2023-01-16","2023-01-31",6,1,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm front-end","2023-01-16","2023-03-15",3,1,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm back-end","2023-02-01","2023-03-15",1,1,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Tester","2023-03-16","2023-03-31",9,1,3);

INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Quản lý dự án Wind","2023-04-01","2023-06-30",7,2,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Phân tích dự án","2023-04-01","2023-04-15",7,2,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Thiết kế database","2023-04-16","2023-04-30",1,2,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm front-end","2023-04-16","2023-06-15",3,2,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm back-end","2023-05-01","2023-06-15",1,2,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Tester","2023-06-16","2023-06-30",5,2,1);

INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Quản lý dự án Zues","2023-05-01","2023-07-31",2,3,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Phân tích dự án","2023-05-01","2023-05-15",2,3,3);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Thiết kế database","2023-05-16","2023-05-31",6,3,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm front-end","2023-05-16","2023-07-15",8,3,2);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm back-end","2023-06-01","2023-07-15",6,3,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Tester","2023-07-16","2023-07-31",9,3,1);

INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Quản lý dự án Hades","2023-08-01","2023-10-31",7,4,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Phân tích dự án","2023-08-01","2023-08-15",7,4,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Thiết kế database","2023-08-16","2023-08-31",1,4,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm front-end","2023-08-16","2023-10-15",8,4,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Làm back-end","2023-09-01","2023-10-15",6,4,1);
INSERT INTO tasks(name,start_date,end_date,user_id,project_id,status_id) VALUE ("Tester","2023-10-16","2023-10-31",5,4,1);



