DROP TABLE IF EXISTS member;
CREATE TABLE member (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(30),
    nickname VARCHAR(30),
    password VARCHAR(30),
    birthday DATE,
    university VARCHAR(30),
    major VARCHAR(30),
    minor VARCHAR(30),
    member_role VARCHAR(30)
);