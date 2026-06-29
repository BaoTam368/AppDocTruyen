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
            user_id TEXT NOT NULL,
            content TEXT NOT NULL,
            created_at DATETIME DEFAULT (datetime('now', '+7 hours')),
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

function getAllMangas({ limit = 20, offset = 0, status = '', tag = '', sort = 'latest' } = {}) {
    const database = getDatabase();
    const paging = normalizePaging({ limit, offset });
    
    let whereClause = '';
    const params = [];
    const conditions = [];
    
    if (status && status.trim() && status.trim() !== 'all') {
        conditions.push('status = ?');
        params.push(status.trim());
    }
    
    if (tag && tag.trim()) {
        conditions.push('tags LIKE ?');
        params.push(`%${tag.trim()}%`);
    }
    
    if (conditions.length > 0) {
        whereClause = 'WHERE ' + conditions.join(' AND ');
    }
    
    const orderBy = buildOrderBy(sort);
    params.push(paging.limit, paging.offset);
    
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
        ${whereClause}
        ORDER BY ${orderBy}
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(...params);
    return mangas.map(manga => ({
        ...manga,
        tags: parseJsonArray(manga.tags),
        availableTranslatedLanguages: parseJsonArray(manga.availableTranslatedLanguages)
    }));
}

function searchMangas(query, { limit = 20, offset = 0, status = '', tag = '', sort = 'latest' } = {}) {
    const database = getDatabase();
    const paging = normalizePaging({ limit, offset });
    
    let whereClause = 'WHERE (title LIKE ? OR description LIKE ?)';
    const params = [];
    const searchPattern = `%${query}%`;
    params.push(searchPattern, searchPattern);
    
    if (status && status.trim() && status.trim() !== 'all') {
        whereClause += ' AND status = ?';
        params.push(status.trim());
    }
    
    if (tag && tag.trim()) {
        whereClause += ' AND tags LIKE ?';
        params.push(`%${tag.trim()}%`);
    }
    
    const orderBy = buildOrderBy(sort);
    params.push(paging.limit, paging.offset);
    
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
        ${whereClause}
        ORDER BY ${orderBy}
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(...params);
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

function buildOrderBy(sort) {
    switch (String(sort || '').toLowerCase()) {
        case 'title_asc': return 'title ASC';
        case 'title_desc': return 'title DESC';
        case 'year_asc': return 'year ASC';
        case 'year_desc': return 'year DESC';
        case 'latest':
        default: return 'updated_at DESC';
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
