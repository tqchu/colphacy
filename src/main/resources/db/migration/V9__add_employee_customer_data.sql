INSERT INTO public.customer (full_name, username, password, phone, is_active, gender, street_address, ward, district,
                             province)
VALUES ('Trương Quang Chứ', 'truongquangchu', '$2a$12$ZanCmnG/O6lwL8wlUXI.I.WsiFVhAH8VZmXSZCAB8UAnpZyeJvD7u',
        '0799024992', true, 'MALE', 'K127/12 Dong ke', 'Hoa Khanh Bac', 'Lien Chieu', 'Da Nang'),
       ('Lê Phi Duy', 'duyle', '$2a$12$ZanCmnG/O6lwL8wlUXI.I.WsiFVhAH8VZmXSZCAB8UAnpZyeJvD7u', '0356789033', true,
        'MALE', 'K127/12 Dong ke', 'Hoa Khanh Bac', 'Lien Chieu', 'Da Nang'),
       ('Nguyễn Thị Kim Chi', 'kimchi', '$2a$12$ZanCmnG/O6lwL8wlUXI.I.WsiFVhAH8VZmXSZCAB8UAnpZyeJvD7u', '0354324343',
        true, 'FEMALE', 'K127/12 Dong ke', 'Hoa Khanh Bac', 'Lien Chieu', 'Da Nang'),
       ('Phạm Thị Quỳnh Linh', 'qlinh', '$2a$12$ZanCmnG/O6lwL8wlUXI.I.WsiFVhAH8VZmXSZCAB8UAnpZyeJvD7u', '0356789017',
        true, 'FEMALE', 'K127/12 Dong ke', 'Hoa Khanh Bac', 'Lien Chieu', 'Da Nang'),
       ('Trần Thị Thu Phượng', 'thuphuong', '$2a$12$ZanCmnG/O6lwL8wlUXI.I.WsiFVhAH8VZmXSZCAB8UAnpZyeJvD7u',
        '0356789023', true, 'FEMALE', 'K127/12 Dong ke', 'Hoa Khanh Bac', 'Lien Chieu', 'Da Nang');

INSERT INTO public.employee (full_name, username, password, phone, is_active, gender, branch_id, role_id)
VALUES ('Trương Quang Chứ', 'truongquangchu', '$2a$12$HE56ktk5nMIud5dN/cXkMuiRtNyogrAOJsYn14h2KsFX25u.tSnEO',
        '0799024992', true, 'MALE', null, 1),
       ('Duy Lê', 'duyle', '$2a$12$HE56ktk5nMIud5dN/cXkMuiRtNyogrAOJsYn14h2KsFX25u.tSnEO', '0356789033', true, 'MALE',
        null, 1),
       ('Nguyễn Thị Kim Chi', 'kimchi', '$2a$12$HE56ktk5nMIud5dN/cXkMuiRtNyogrAOJsYn14h2KsFX25u.tSnEO', '0354324343',
        true, 'FEMALE', null, 1),
       ('Phạm Thị Quỳnh Linh', 'qlinh', '$2a$12$HE56ktk5nMIud5dN/cXkMuiRtNyogrAOJsYn14h2KsFX25u.tSnEO', '0356789017',
        true, 'FEMALE', null, 1),
       ('Trần Thị Thu Phượng', 'thuphuong', '$2a$12$HE56ktk5nMIud5dN/cXkMuiRtNyogrAOJsYn14h2KsFX25u.tSnEO',
        '0356789023', true, 'FEMALE', null, 1);

