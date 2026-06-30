const databaseService = require('./databaseService');
const userService = require('./userService');

function getComments({mangaId, chapterId, limit = 50, offset = 0} = {}) {
    const database = databaseService.getDatabase();
    const filters = [];
    const params = [];
    const mangaIdFilter = normalizeNullable(mangaId);
    const chapterIdFilter = normalizeNullable(chapterId);

    if (mangaIdFilter) {
        filters.push('c.manga_id = ?');
        params.push(mangaIdFilter);
    }

    if (chapterIdFilter) {
        filters.push('c.chapter_id = ?');
        params.push(chapterIdFilter);
    } else if (mangaIdFilter) {
        filters.push('c.chapter_id IS NULL');
    }

    const whereClause = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
    const stmt = database.prepare(`
        SELECT
            c.id,
            c.manga_id,
            c.chapter_id,
            c.user_id,
            c.content,
            c.created_at,
            c.updated_at,
            u.display_name,
            u.avatar_url
        FROM comments c
        LEFT JOIN users u ON c.user_id = u.user_id
        ${whereClause}
        ORDER BY c.created_at DESC
        LIMIT ? OFFSET ?
    `);

    const rows = stmt.all(...params, clampNumber(limit, 1, 100, 50), clampNumber(offset, 0, 10000, 0));
    return rows.map(mapComment);
}

function createComment(payload = {}) {
    const requestedUserId = normalizeText(payload.userId || payload.user_id);
    const mangaId = normalizeNullable(payload.mangaId || payload.manga_id);
    const chapterId = normalizeNullable(payload.chapterId || payload.chapter_id);
    const content = normalizeText(payload.content);

    if (!requestedUserId) {
        throw createHttpError(400, 'User is required.');
    }

    if (!content) {
        throw createHttpError(400, 'Missing comment content');
    }

    const user = userService.createOrUpdateUser({ userId: requestedUserId });
    const userId = user && user.userId ? user.userId : requestedUserId;

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        INSERT INTO comments (manga_id, chapter_id, user_id, content)
        VALUES (?, ?, ?, ?)
    `);

    const result = stmt.run(mangaId, chapterId, userId, content);
    return getCommentById(result.lastInsertRowid);
}

function updateComment(commentId, payload = {}) {
    const existing = getCommentById(commentId);
    if (!existing) {
        throw createHttpError(404, 'Comment not found');
    }

    const content = payload.content === undefined ? existing.content : normalizeText(payload.content);
    if (!content) {
        throw createHttpError(400, 'Missing comment content');
    }

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        UPDATE comments
        SET content = ?, updated_at = datetime('now', '+7 hours')
        WHERE id = ?
    `);

    stmt.run(content, commentId);
    return getCommentById(commentId);
}

function deleteComment(commentId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare('DELETE FROM comments WHERE id = ?');
    const result = stmt.run(commentId);
    return result.changes > 0;
}

function getCommentById(commentId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        SELECT
            c.id,
            c.manga_id,
            c.chapter_id,
            c.user_id,
            c.content,
            c.created_at,
            c.updated_at,
            u.display_name,
            u.avatar_url
        FROM comments c
        LEFT JOIN users u ON c.user_id = u.user_id
        WHERE c.id = ?
    `);

    const row = stmt.get(commentId);
    return row ? mapComment(row) : null;
}

function mapComment(row) {
    const displayName = row.display_name || row.user_id || 'Unknown';
    const avatarUrl = row.avatar_url || '';

    return {
        id: row.id,
        mangaId: row.manga_id || null,
        chapterId: row.chapter_id || null,
        userId: row.user_id,
        username: displayName,
        displayName,
        content: row.content,
        avatarUrl,
        createdAt: row.created_at,
        updatedAt: row.updated_at,
        manga_id: row.manga_id || null,
        chapter_id: row.chapter_id || null,
        user_id: row.user_id,
        display_name: displayName,
        avatar_url: avatarUrl,
        created_at: row.created_at,
        updated_at: row.updated_at
    };
}

function normalizeText(value) {
    return value === undefined || value === null ? '' : String(value).trim();
}

function normalizeNullable(value) {
    const text = normalizeText(value);
    if (!text) return null;
    const lowerText = text.toLowerCase();
    return lowerText === 'null' || lowerText === 'undefined' ? null : text;
}

function clampNumber(value, min, max, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue)) return fallback;
    return Math.max(min, Math.min(max, numberValue));
}

function createHttpError(statusCode, message) {
    const error = new Error(message);
    error.statusCode = statusCode;
    return error;
}

module.exports = {
    getComments,
    createComment,
    getCommentById,
    updateComment,
    deleteComment
};