SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM badge;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO badge (id, description, image_url, name, price) VALUES
(1, '금색 훈장입니다.', 'images/badges/gold_badge.png', 'Gold Badge', 1000),
(2, '은색 훈장입니다.', 'images/badges/silver_badge.png', 'Silver Badge', 800),
(3, '동색 훈장입니다.', 'images/badges/bronze_badge.png', 'Bronze Badge', 500),
(4, '다이아몬드 훈장입니다.', 'images/badges/diamond_badge.svg', 'Diamond Badge', 1500);
