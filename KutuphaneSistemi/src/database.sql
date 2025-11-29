/* Kütüphane projesi veritabanı kurulum kodları */

CREATE DATABASE IF NOT EXISTS kutuphane_db;

USE kutuphane_db;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) DEFAULT 'Genel',
    page_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'RAFT'
    );

/* Örnek veri */
INSERT INTO books (title, author, category, page_count, status)
VALUES ('Suç ve Ceza', 'Dostoyevski', 'Roman', 687, 'RAFT');