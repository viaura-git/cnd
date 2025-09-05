-- 기초데이터 등록
-- 1. 권한의 종류 등록
INSERT INTO ROLES (ROLE_NAME) VALUES
          ('ROLE_USER'),('ROLE_ADMIN'), ('ROLE_DEVELOPER');

-- 2. admin 등록
INSERT INTO users (
    user_id, user_name, email, password, phone, enabled, reg_date
) VALUES (
    'admin',
    '어드민',
    'admin@lea.com',
    '$2a$10$7OJPaNi1GZmH4j6UT5AOyONuor9HXuaBYj1Ki0Vbpf8tKOJT10i42', -- "1234"
    '000-1111-2222',
    true,
    now()
);

-- 2.1 admin 권한 연결
-- 초기 데이터이므로 admin의 id는 1 일 것이라 가정하는 것 뿐이다
INSERT INTO user_roles (uid, role_name)
VALUES (1, 'ROLE_ADMIN');

---

-- 3. aaa user 등록
INSERT INTO users (
    user_id, email, user_name, password, phone, enabled, reg_date
) VALUES (
             'aaa',
             'aaa@naver.com',
             '에이',
             '$2a$10$7OJPaNi1GZmH4j6UT5AOyONuor9HXuaBYj1Ki0Vbpf8tKOJT10i42', -- "1234"
             '000-2222-3333',
             true,
             now()
         );

-- 3.1 권한 연결
-- 초기 데이터이므로 aaa의 id는 2 일 것이라 가정하는 것 뿐이다
INSERT INTO user_roles (uid, role_name)
VALUES (2, 'ROLE_USER');

----

-- 4. developer user 등록
INSERT INTO users (
    user_id, email, user_name, password, phone, enabled, reg_date
) VALUES (
             'dev',
             'dev@naver.com',
             '데브',
             '$2a$10$7OJPaNi1GZmH4j6UT5AOyONuor9HXuaBYj1Ki0Vbpf8tKOJT10i42', -- "1234"
             '000-333-3333',
             true,
             now()
         );

-- 4.1 권한 연결
-- 초기 데이터이므로 aaa의 id는 3 일 것이라 가정하는 것 뿐이다
INSERT INTO user_roles (uid, role_name)
VALUES (3, 'ROLE_DEVELOPER');

