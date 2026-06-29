const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, '../../mangas.db');
let db;

function getDatabase() {
    if (!db) {
        db = new Database(dbPath);
        db.pragma('journal_mode = WAL');
        db.pragma('foreign_keys = ON');
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
            content_rating TEXT,
            available_translated_languages TEXT,
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

        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id TEXT NOT NULL UNIQUE,
            display_name TEXT,
            email TEXT,
            avatar_url TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS comments (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            manga_id TEXT,
            chapter_id TEXT,
            user_id TEXT NOT NULL,
            content TEXT NOT NULL,
            created_at DATETIME DEFAULT (datetime('now', '+7 hours')),
            updated_at DATETIME DEFAULT (datetime('now', '+7 hours')),
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS posts (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id TEXT NOT NULL,
            content TEXT NOT NULL,
            image_url TEXT,
            like_count INTEGER DEFAULT 0,
            created_at DATETIME DEFAULT (datetime('now', '+7 hours')),
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS post_likes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id TEXT NOT NULL,
            post_id INTEGER NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
            UNIQUE(user_id, post_id)
        );

        CREATE INDEX IF NOT EXISTS idx_mangas_title ON mangas(title);
        CREATE INDEX IF NOT EXISTS idx_chapters_manga_id ON chapters(manga_id);
        CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(user_id);
    `);

    ensureMangaMetadataColumns();
    ensureCommentColumns();
}

function saveManga(manga) {
    const database = getDatabase();
    const stmt = database.prepare(`
        INSERT OR REPLACE INTO mangas 
        (id, title, description, cover_url, status, year, tags, latest_chapter, content_rating, available_translated_languages, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    `);
    
    stmt.run(
        manga.mangaId,
        manga.title,
        manga.description,
        manga.coverUrl,
        manga.status,
        manga.year,
        JSON.stringify(manga.tags || []),
        manga.latestChapter,
        manga.contentRating,
        JSON.stringify(manga.availableTranslatedLanguages || [])
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
    const paging = normalizePaging({ limit, offset });
    const stmt = database.prepare(`
        SELECT 
            id as mangaId,
            title,
            description,
            cover_url as coverUrl,
            status,
            year,
            tags,
            latest_chapter as latestChapter,
            content_rating as contentRating,
            available_translated_languages as availableTranslatedLanguages
        FROM mangas
        ORDER BY updated_at DESC
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(paging.limit, paging.offset);
    return mangas.map(manga => ({
        ...manga,
        tags: parseJsonArray(manga.tags),
        availableTranslatedLanguages: parseJsonArray(manga.availableTranslatedLanguages)
    }));
}

function searchMangas(query, { limit = 20, offset = 0 } = {}) {
    const database = getDatabase();
    const paging = normalizePaging({ limit, offset });
    const stmt = database.prepare(`
        SELECT 
            id as mangaId,
            title,
            description,
            cover_url as coverUrl,
            status,
            year,
            tags,
            latest_chapter as latestChapter,
            content_rating as contentRating,
            available_translated_languages as availableTranslatedLanguages
        FROM mangas
        WHERE title LIKE ? OR description LIKE ?
        ORDER BY updated_at DESC
        LIMIT ? OFFSET ?
    `);
    
    const searchPattern = `%${query}%`;
    const mangas = stmt.all(searchPattern, searchPattern, paging.limit, paging.offset);
    return mangas.map(manga => ({
        ...manga,
        tags: parseJsonArray(manga.tags),
        availableTranslatedLanguages: parseJsonArray(manga.availableTranslatedLanguages)
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
            latest_chapter as latestChapter,
            content_rating as contentRating,
            available_translated_languages as availableTranslatedLanguages
        FROM mangas
        WHERE id = ?
    `);
    
    const manga = stmt.get(mangaId);
    if (!manga) return null;
    
    return {
        ...manga,
        tags: parseJsonArray(manga.tags),
        availableTranslatedLanguages: parseJsonArray(manga.availableTranslatedLanguages)
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

function ensureMangaMetadataColumns() {
    ensureColumn('mangas', 'content_rating', 'TEXT');
    ensureColumn('mangas', 'available_translated_languages', 'TEXT');
}

function ensureCommentColumns() {
    ensureColumn('comments', 'manga_id', 'TEXT');
    ensureColumn('comments', 'chapter_id', 'TEXT');
    ensureColumn('comments', 'updated_at', 'DATETIME');
    db.exec("UPDATE comments SET updated_at = created_at WHERE updated_at IS NULL");
    db.exec(`
        CREATE INDEX IF NOT EXISTS idx_comments_manga_id ON comments(manga_id);
        CREATE INDEX IF NOT EXISTS idx_comments_chapter_id ON comments(chapter_id);
        CREATE INDEX IF NOT EXISTS idx_comments_manga_chapter ON comments(manga_id, chapter_id);
    `);
}

function ensureColumn(tableName, columnName, definition) {
    const columns = db.prepare(`PRAGMA table_info(${tableName})`).all();
    const exists = columns.some((column) => column.name === columnName);
    if (!exists) {
        db.exec(`ALTER TABLE ${tableName} ADD COLUMN ${columnName} ${definition}`);
    }
}

function parseJsonArray(value) {
    if (!value) return [];
    try {
        const parsed = JSON.parse(value);
        return Array.isArray(parsed) ? parsed : [];
    } catch (error) {
        return [];
    }
}

function normalizePaging({ limit = 20, offset = 0 } = {}) {
    return {
        limit: clampNumber(limit, 1, 200, 20),
        offset: clampNumber(offset, 0, 10000, 0)
    };
}

function clampNumber(value, min, max, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue)) return fallback;
    return Math.max(min, Math.min(max, numberValue));
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