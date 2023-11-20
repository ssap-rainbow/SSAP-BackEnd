CREATE SCHEMA IF NOT EXISTS `krampoline` DEFAULT CHARACTER SET utf8mb4;

GRANT ALL ON *.* TO 'root'@'localhost' IDENTIFIED BY 'root' WITH GRANT OPTION;
GRANT ALL ON krampoline.* TO 'root'@'localhost';
GRANT ALL ON *.* TO 'root'@'%' IDENTIFIED BY 'root' WITH GRANT OPTION;
GRANT ALL ON krampoline.* TO 'root'@'%';
FLUSH PRIVILEGES;

USE `krampoline`;

DROP TABLE IF EXISTS `sample_data`;
CREATE TABLE `sample_data` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `detail` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO sample_data (`id`,`detail`) VALUES ('1', 'Hello DKOS!');

-- 테이블 생성 (존재하지 않을 경우에만)
CREATE TABLE IF NOT EXISTS categories (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    category_name varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 데이터 삽입
INSERT INTO categories (category_name) VALUES ('배달·퀵') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('청소') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('운반·수리') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('동행·육아') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('펫') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('역할대행') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('알바') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('벌레잡기') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
INSERT INTO categories (category_name) VALUES ('기타') ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);


-- detailed_items 테이블 생성 (존재하지 않을 경우에만)
CREATE TABLE IF NOT EXISTS detailed_items (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    category_id bigint(20) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    KEY category_id (category_id),
    CONSTRAINT detail_items_ibfk_1 FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- detailed_items에 데이터 삽입
INSERT INTO detailed_items (category_id, name) VALUES (1, '편의점 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '음식 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '음식·주류 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '물품 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '퀵 배송');
INSERT INTO detailed_items (category_id, name) VALUES (1, '마트 장보기');
INSERT INTO detailed_items (category_id, name) VALUES (1, '우편물 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '카페 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '꽃 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '세탁물 배달');
INSERT INTO detailed_items (category_id, name) VALUES (1, '기타 배달·퀵');
INSERT INTO detailed_items (category_id, name) VALUES (2, '집·원룸 청소');
INSERT INTO detailed_items (category_id, name) VALUES (2, '사무실·가게 청소');
INSERT INTO detailed_items (category_id, name) VALUES (2, '이사·입주 청소');
INSERT INTO detailed_items (category_id, name) VALUES (2, '정리 정돈');
INSERT INTO detailed_items (category_id, name) VALUES (2, '쓰레기·분리수거');
INSERT INTO detailed_items (category_id, name) VALUES (2, '가구 폐기');
INSERT INTO detailed_items (category_id, name) VALUES (2, '빨래·세탁');
INSERT INTO detailed_items (category_id, name) VALUES (2, '설거지');
INSERT INTO detailed_items (category_id, name) VALUES (2, '화장실 청소');
INSERT INTO detailed_items (category_id, name) VALUES (3, '물품 운반');
INSERT INTO detailed_items (category_id, name) VALUES (3, '가구 운반');
INSERT INTO detailed_items (category_id, name) VALUES (3, '가구 설치·조립');
INSERT INTO detailed_items (category_id, name) VALUES (3, '이삿짐 옮기기');
INSERT INTO detailed_items (category_id, name) VALUES (3, '가구 폐기');
INSERT INTO detailed_items (category_id, name) VALUES (3, '등·조명 교체');
INSERT INTO detailed_items (category_id, name) VALUES (3, '커튼 설치');
INSERT INTO detailed_items (category_id, name) VALUES (3, '못 박기');
INSERT INTO detailed_items (category_id, name) VALUES (3, '집 수리');
INSERT INTO detailed_items (category_id, name) VALUES (3, '가구 수리');
INSERT INTO detailed_items (category_id, name) VALUES (3, '용달');
INSERT INTO detailed_items (category_id, name) VALUES (3, '기타 운반·수리');
INSERT INTO detailed_items (category_id, name) VALUES (4, '등·하원');
INSERT INTO detailed_items (category_id, name) VALUES (4, '동행');
INSERT INTO detailed_items (category_id, name) VALUES (4, '육아');
INSERT INTO detailed_items (category_id, name) VALUES (4, '기타 동행·육아');
INSERT INTO detailed_items (category_id, name) VALUES (5, '펫 산책');
INSERT INTO detailed_items (category_id, name) VALUES (5, '펫 시팅');
INSERT INTO detailed_items (category_id, name) VALUES (5, '펫 목욕');
INSERT INTO detailed_items (category_id, name) VALUES (5, '펫 미용');
INSERT INTO detailed_items (category_id, name) VALUES (5, '펫 사료주기');
INSERT INTO detailed_items (category_id, name) VALUES (5, '기타');
INSERT INTO detailed_items (category_id, name) VALUES (6, '줄 서기');
INSERT INTO detailed_items (category_id, name) VALUES (6, '택배 부치기');
INSERT INTO detailed_items (category_id, name) VALUES (6, '현장 확인');
INSERT INTO detailed_items (category_id, name) VALUES (6, '중고 거래 대행');
INSERT INTO detailed_items (category_id, name) VALUES (6, '이벤트 참여');
INSERT INTO detailed_items (category_id, name) VALUES (6, '티켓 예매');
INSERT INTO detailed_items (category_id, name) VALUES (6, '관공서 업무 대행');
INSERT INTO detailed_items (category_id, name) VALUES (6, '녹색어머니 대행');
INSERT INTO detailed_items (category_id, name) VALUES (6, '출력·팩스·도장');
INSERT INTO detailed_items (category_id, name) VALUES (6, '하객 대행');
INSERT INTO detailed_items (category_id, name) VALUES (6, '은행 업무 대행');
INSERT INTO detailed_items (category_id, name) VALUES (6, '경조사 참석');
INSERT INTO detailed_items (category_id, name) VALUES (6, '기타 역할 대행');
INSERT INTO detailed_items (category_id, name) VALUES (7, '일일 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '단기 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '식당 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '포장 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '행사 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '전단지 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '편의점 알바');
INSERT INTO detailed_items (category_id, name) VALUES (7, '기타 알바');
INSERT INTO detailed_items (category_id, name) VALUES (8, '바퀴벌레 잡기');
INSERT INTO detailed_items (category_id, name) VALUES (8, '곤충 잡기');
INSERT INTO detailed_items (category_id, name) VALUES (8, '쥐 잡기');
INSERT INTO detailed_items (category_id, name) VALUES (8, '기타 벌레 잡기');
INSERT INTO detailed_items (category_id, name) VALUES (9, '단순 심부름');
INSERT INTO detailed_items (category_id, name) VALUES (9, '현장 확인');
INSERT INTO detailed_items (category_id, name) VALUES (9, '비대면 알바');
INSERT INTO detailed_items (category_id, name) VALUES (9, '인터넷 업무');
INSERT INTO detailed_items (category_id, name) VALUES (9, '문서·글쓰기');
INSERT INTO detailed_items (category_id, name) VALUES (9, '고민 상담');
INSERT INTO detailed_items (category_id, name) VALUES (9, '안심 귀가');
INSERT INTO detailed_items (category_id, name) VALUES (9, '온라인 예매');
INSERT INTO detailed_items (category_id, name) VALUES (9, '비대면 과외');
INSERT INTO detailed_items (category_id, name) VALUES (9, '가이드');
INSERT INTO detailed_items (category_id, name) VALUES (9, '게임 공략');
INSERT INTO detailed_items (category_id, name) VALUES (9, '킥보드·자전거 이동');
INSERT INTO detailed_items (category_id, name) VALUES (9, '기타 심부름');