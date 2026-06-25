const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, '../../mangas.db');
let db;

function getDatabase() {
    if (!db) {
        db = new Database(dbPath);
        db.pragma('journal_mode = WAL');
        initializeDatabase();
    }
    return db;
}

function initializeDatabase() {
    db.exec(`
        CREATE TABLE IF NOT EXISTS mangas (
            id TEXT PRIMARY KEY,
            title TEXT NOT NULL,
            description TEXT,
            cover_url TEXT,
            status TEXT,
            year INTEGER,
            tags TEXT,
            latest_chapter TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS chapters (
            id TEXT PRIMARY KEY,
            manga_id TEXT NOT NULL,
            chapter_number TEXT,
            chapter_name TEXT,
            language TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (manga_id) REFERENCES mangas(id) ON DELETE CASCADE
        );

        CREATE INDEX IF NOT EXISTS idx_mangas_title ON mangas(title);
        CREATE INDEX IF NOT EXISTS idx_chapters_manga_id ON chapters(manga_id);
    `);
}

function saveManga(manga) {
    const database = getDatabase();
    const stmt = database.prepare(`
        INSERT OR REPLACE INTO mangas 
        (id, title, description, cover_url, status, year, tags, latest_chapter, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    `);
    
    stmt.run(
        manga.mangaId,
        manga.title,
        manga.description,
        manga.coverUrl,
        manga.status,
        manga.year,
        JSON.stringify(manga.tags || []),
        manga.latestChapter
    );
}

function saveChapter(chapter) {
    const database = getDatabase();
    const stmt = database.prepare(`
        INSERT OR REPLACE INTO chapters 
        (id, manga_id, chapter_number, chapter_name, language)
        VALUES (?, ?, ?, ?, ?)
    `);
    
    stmt.run(
        chapter.chapterId,
        chapter.mangaId,
        chapter.chapterNumber,
        chapter.chapterName,
        chapter.language
    );
}

function getAllMangas({ limit = 20, offset = 0 } = {}) {
    const database = getDatabase();
    const stmt = database.prepare(`
        SELECT 
            id as mangaId,
            title,
            description,
            cover_url as coverUrl,
            status,
            year,
            tags,
            latest_chapter as latestChapter
        FROM mangas
        ORDER BY updated_at DESC
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(limit, offset);
    return mangas.map(manga => ({
        ...manga,
        tags: manga.tags ? JSON.parse(manga.tags) : []
    }));
}

function searchMangas(query, { limit = 20, offset = 0 } = {}) {
    const database = getDatabase();
    const stmt = database.prepare(`
        SELECT 
            id as mangaId,
            title,
            description,
            cover_url as coverUrl,
            status,
            year,
            tags,
            latest_chapter as latestChapter
        FROM mangas
        WHERE title LIKE ? OR description LIKE ?
        ORDER BY updated_at DESC
        LIMIT ? OFFSET ?
    `);
    
    const searchPattern = `%${query}%`;
    const mangas = stmt.all(searchPattern, searchPattern, limit, offset);
    return mangas.map(manga => ({
        ...manga,
        tags: manga.tags ? JSON.parse(manga.tags) : []
    }));
}

function getMangaById(mangaId) {
    const database = getDatabase();
    const stmt = database.prepare(`
        SELECT 
            id as mangaId,
            title,
            description,
            cover_url as coverUrl,
            status,
            year,
            tags,
            latest_chapter as latestChapter
        FROM mangas
        WHERE id = ?
    `);
    
    const manga = stmt.get(mangaId);
    if (!manga) return null;
    
    return {
        ...manga,
        tags: manga.tags ? JSON.parse(manga.tags) : []
    };
}

function getMangaChapters(mangaId) {
    const database = getDatabase();
    const stmt = database.prepare(`
        SELECT 
            id as chapterId,
            manga_id as mangaId,
            chapter_number as chapterNumber,
            chapter_name as chapterName,
            language,
            created_at as createdAt
        FROM chapters
        WHERE manga_id = ?
        ORDER BY chapter_number ASC
    `);
    
    return stmt.all(mangaId);
}

function closeDatabase() {
    if (db) {
        db.close();
        db = null;
    }
}

module.exports = {
    getDatabase,
    saveManga,
    saveChapter,
    getAllMangas,
    searchMangas,
    getMangaById,
    getMangaChapters,
    closeDatabase
};
