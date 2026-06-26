const databaseService = require('./databaseService');
const userService = require('./userService');

function getPosts({ userId, mangaId, limit = 50, offset = 0 } = {}) {
    const database = databaseService.getDatabase();
    const filters = [];
    const params = [];

    if (userId) {
        filters.push('user_id = ?');
        params.push(userId);
    }

    if (mangaId) {
        filters.push('manga_id = ?');
        params.push(mangaId);
    }

    const whereClause = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
    const stmt = database.prepare(`
        SELECT
            id,
            user_id,
            title,
            content,
            image_url,
            manga_id,
            created_at,
            updated_at
        FROM posts
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT ? OFFSET ?
    `);

    const rows = stmt.all(...params, clampNumber(limit, 1, 100, 50), clampNumber(offset, 0, 10000, 0));
    return rows.map(mapPost);
}

function getPostById(postId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        SELECT
            id,
            user_id,
            title,
            content,
            image_url,
            manga_id,
            created_at,
            updated_at
        FROM posts
        WHERE id = ?
    `);

    const row = stmt.get(postId);
    return row ? mapPost(row) : null;
}

function createPost(payload = {}) {
    const userId = normalizeText(payload.userId || payload.user_id || 'local_user');
    const title = normalizeNullable(payload.title);
    const content = normalizeText(payload.content);
    const imageUrl = normalizeNullable(payload.imageUrl || payload.image_url);
    const mangaId = normalizeNullable(payload.mangaId || payload.manga_id);

    if (!content) {
        throw createHttpError(400, 'Missing post content');
    }

    userService.createOrUpdateUser({ userId });

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        INSERT INTO posts (user_id, title, content, image_url, manga_id, updated_at)
        VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    `);

    const result = stmt.run(userId, title, content, imageUrl, mangaId);
    return getPostById(result.lastInsertRowid);
}

function updatePost(postId, payload = {}) {
    const existing = getPostById(postId);
    if (!existing) {
        throw createHttpError(404, 'Post not found');
    }

    const title = hasOwn(payload, 'title') ? normalizeNullable(payload.title) : existing.title;
    const content = hasOwn(payload, 'content') ? normalizeText(payload.content) : existing.content;
    const imageUrl = hasOwn(payload, 'imageUrl')
        ? normalizeNullable(payload.imageUrl)
        : hasOwn(payload, 'image_url')
            ? normalizeNullable(payload.image_url)
            : existing.imageUrl;
    const mangaId = hasOwn(payload, 'mangaId')
        ? normalizeNullable(payload.mangaId)
        : hasOwn(payload, 'manga_id')
            ? normalizeNullable(payload.manga_id)
            : existing.mangaId;

    if (!content) {
        throw createHttpError(400, 'Missing post content');
    }

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        UPDATE posts
        SET title = ?, content = ?, image_url = ?, manga_id = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
    `);

    stmt.run(title, content, imageUrl, mangaId, postId);
    return getPostById(postId);
}

function deletePost(postId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare('DELETE FROM posts WHERE id = ?');
    const result = stmt.run(postId);
    return result.changes > 0;
}

function mapPost(row) {
    return {
        id: row.id,
        userId: row.user_id,
        title: row.title || '',
        content: row.content,
        imageUrl: row.image_url,
        mangaId: row.manga_id,
        createdAt: row.created_at,
        updatedAt: row.updated_at
    };
}

function normalizeText(value) {
    return value === undefined || value === null ? '' : String(value).trim();
}

function normalizeNullable(value) {
    const text = normalizeText(value);
    return text || null;
}

function clampNumber(value, min, max, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue)) return fallback;
    return Math.max(min, Math.min(max, numberValue));
}

function hasOwn(object, key) {
    return Object.prototype.hasOwnProperty.call(object, key);
}

function createHttpError(statusCode, message) {
    const error = new Error(message);
    error.statusCode = statusCode;
    return error;
}

module.exports = {
    getPosts,
    getPostById,
    createPost,
    updatePost,
    deletePost
};
