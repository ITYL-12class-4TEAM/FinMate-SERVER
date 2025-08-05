-- 알림 테이블
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    target_url VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    read_at DATETIME,
    related_data JSON,
    INDEX idx_member_id_created_at (member_id, created_at DESC),
    INDEX idx_member_id_is_read (member_id, is_read),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- 알림 설정 테이블
CREATE TABLE notification_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_member_type (member_id, type),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- 알림 타입별 기본 설정 데이터
INSERT INTO notification_settings (member_id, type, is_enabled, created_at, updated_at)
VALUES
(1, 'POST_COMMENT', TRUE, NOW(), NOW()),
(1, 'POST_LIKE', TRUE, NOW(), NOW()),
(1, 'COMMENT_REPLY', TRUE, NOW(), NOW()),
(1, 'HOT_POST', TRUE, NOW(), NOW()),
(1, 'SYSTEM', TRUE, NOW(), NOW()),
(1, 'PRODUCT_RECOMMENDATION', FALSE, NOW(), NOW());
