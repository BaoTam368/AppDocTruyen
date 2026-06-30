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
            last_synced_at DATETIME,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS chapters (
            id TEXT PRIMARY KEY,
            manga_id TEXT NOT NULL,
            chapter_number TEXT,
            chapter_name TEXT,
            language TEXT,
            publish_at TEXT,
            readable_at TEXT,
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
    ensureChapterColumns();
    ensureCommentColumns();
    ensurePostColumns();
    ensureGroupMangaCountCache();
}

function saveManga(manga) {
    if (!manga || !manga.mangaId || !manga.title) {
        return;
    }

    const database = getDatabase();
    const stmt = database.prepare(`
        INSERT INTO mangas 
        (id, title, description, cover_url, status, year, tags, latest_chapter, content_rating, available_translated_languages, last_synced_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT(id) DO UPDATE SET
            title = excluded.title,
            description = excluded.description,
            cover_url = COALESCE(NULLIF(excluded.cover_url, ''), mangas.cover_url),
            status = excluded.status,
            year = excluded.year,
            tags = excluded.tags,
            latest_chapter = excluded.latest_chapter,
            content_rating = excluded.content_rating,
            available_translated_languages = excluded.available_translated_languages,
            last_synced_at = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
    `);
    
    stmt.run(
        manga.mangaId,
        manga.title,
        manga.description,
        normalizeCoverUrl(manga.coverUrl),
        manga.status,
        manga.year,
        JSON.stringify(manga.tags || []),
        manga.latestChapter,
        manga.contentRating,
        JSON.stringify(manga.availableTranslatedLanguages || [])
    );
}

function saveChapter(chapter) {
    if (!chapter || !chapter.chapterId || !chapter.mangaId) {
        return;
    }

    const database = getDatabase();
    const stmt = database.prepare(`
        INSERT OR REPLACE INTO chapters 
        (id, manga_id, chapter_number, chapter_name, language, publish_at, readable_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    `);
    const chapterNumber = firstNonBlank(chapter.chapterNumber, chapter.chapter);
    
    stmt.run(
        chapter.chapterId,
        chapter.mangaId,
        chapterNumber,
        firstNonBlank(chapter.chapterName, chapter.title, chapterNumber ? `Chapter ${chapterNumber}` : 'Chapter'),
        firstNonBlank(chapter.language, chapter.translatedLanguage),
        firstNonBlank(chapter.publishAt),
        firstNonBlank(chapter.readableAt)
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
            available_translated_languages as availableTranslatedLanguages,
            last_synced_at as lastSyncedAt
        FROM mangas
        ${whereClause}
        ORDER BY ${orderBy}
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(...params);
    return mangas.map(manga => ({
        ...manga,
        coverUrl: normalizeCoverUrl(manga.coverUrl),
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
            available_translated_languages as availableTranslatedLanguages,
            last_synced_at as lastSyncedAt
        FROM mangas
        ${whereClause}
        ORDER BY ${orderBy}
        LIMIT ? OFFSET ?
    `);
    
    const mangas = stmt.all(...params);
    return mangas.map(manga => ({
        ...manga,
        coverUrl: normalizeCoverUrl(manga.coverUrl),
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
            available_translated_languages as availableTranslatedLanguages,
            last_synced_at as lastSyncedAt
        FROM mangas
        WHERE id = ?
    `);
    
    const manga = stmt.get(mangaId);
    if (!manga) return null;
    
    return {
        ...manga,
        coverUrl: normalizeCoverUrl(manga.coverUrl),
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
            chapter_number as chapter,
            chapter_number as chapterNumber,
            chapter_name as title,
            chapter_name as chapterName,
            language,
            language as translatedLanguage,
            publish_at as publishAt,
            readable_at as readableAt,
            created_at as createdAt
        FROM chapters
        WHERE manga_id = ?
    `);
    
    return sortChapters(stmt.all(mangaId));
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
    ensureColumn('mangas', 'last_synced_at', 'DATETIME');
    ensureColumn('mangas', 'cover_url', 'TEXT');
}

function ensurePostColumns() {
    ensureColumn('posts', 'like_count', 'INTEGER DEFAULT 0');
}

function ensureChapterColumns() {
    ensureColumn('chapters', 'publish_at', 'TEXT');
    ensureColumn('chapters', 'readable_at', 'TEXT');
}

function ensureGroupMangaCountCache() {
    db.exec(`
        CREATE TABLE IF NOT EXISTS group_manga_count_cache (
            group_id TEXT PRIMARY KEY,
            manga_count INTEGER NOT NULL DEFAULT 0,
            cached_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    `);
}

function getCachedGroupMangaCount(groupId) {
    const database = getDatabase();
    const row = database.prepare(
        `SELECT manga_count FROM group_manga_count_cache
         WHERE group_id = ? AND cached_at > datetime('now', '-24 hours')`
    ).get(groupId);
    return row ? row.manga_count : null;
}

function setCachedGroupMangaCount(groupId, mangaCount) {
    const database = getDatabase();
    database.prepare(
        `INSERT OR REPLACE INTO group_manga_count_cache (group_id, manga_count, cached_at)
         VALUES (?, ?, CURRENT_TIMESTAMP)`
    ).run(groupId, mangaCount);
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

function sortChapters(chapters) {
    return (chapters || []).sort((a, b) => {
        const chapterA = parseChapterNumber(a && (a.chapter || a.chapterNumber));
        const chapterB = parseChapterNumber(b && (b.chapter || b.chapterNumber));

        if (chapterA !== chapterB) {
            return chapterA - chapterB;
        }

        return chapterDateValue(a) - chapterDateValue(b);
    });
}

function parseChapterNumber(chapter) {
    if (chapter === null || chapter === undefined) {
        return Number.POSITIVE_INFINITY;
    }

    const value = String(chapter).trim();
    if (!value) {
        return Number.POSITIVE_INFINITY;
    }

    const parsed = Number.parseFloat(value.replace(',', '.'));
    return Number.isFinite(parsed) ? parsed : Number.POSITIVE_INFINITY;
}

function chapterDateValue(chapter) {
    const value = chapter && (chapter.publishAt || chapter.readableAt || chapter.createdAt);
    const time = value ? new Date(value).getTime() : 0;
    return Number.isFinite(time) ? time : 0;
}

function firstNonBlank(...values) {
    for (const value of values) {
        if (value !== undefined && value !== null && String(value).trim()) {
            return String(value).trim();
        }
    }
    return '';
}
function normalizeCoverUrl(value) {
    const coverUrl = value === undefined || value === null ? '' : String(value).trim();
    if (!coverUrl) return '';
    if (!coverUrl.includes('/covers/')) return coverUrl;
    if (coverUrl.endsWith('.256.jpg') || coverUrl.endsWith('.512.jpg')) return coverUrl;
    return `${coverUrl}.256.jpg`;
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

function getMangaCount() {
    const database = getDatabase();
    const row = database.prepare('SELECT COUNT(*) as count FROM mangas').get();
    return row ? row.count : 0;
}

module.exports = {
    getDatabase,
    saveManga,
    saveChapter,
    getAllMangas,
    searchMangas,
    getMangaById,
    getMangaChapters,
    getMangaCount,
    getCachedGroupMangaCount,
    setCachedGroupMangaCount,
    closeDatabase
};