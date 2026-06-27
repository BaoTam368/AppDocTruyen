const databaseService = require('./databaseService');
const userService = require('./userService');

function getComments({ mangaId, chapterId, limit = 50, offset = 0 } = {}) {
    const database = databaseService.getDatabase();
    const filters = [];
    const params = [];

    if (mangaId) {
        filters.push('manga_id = ?');
        params.push(mangaId);
    }

    if (chapterId) {
        filters.push('chapter_id = ?');
        params.push(chapterId);
    }

    const whereClause = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
    const stmt = database.prepare(`
        SELECT
            id,
            user_id,
            manga_id,
            chapter_id,
            content,
            created_at,
            updated_at
        FROM comments
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT ? OFFSET ?
    `);

    const rows = stmt.all(...params, clampNumber(limit, 1, 100, 50), clampNumber(offset, 0, 10000, 0));
    return rows.map(mapComment);
}

function createComment(payload = {}) {
    const userId = normalizeText(payload.userId || payload.user_id || 'local_user');
    const mangaId = normalizeText(payload.mangaId || payload.manga_id);
    const chapterId = normalizeNullable(payload.chapterId || payload.chapter_id);
    const content = normalizeText(payload.content);

    if (!mangaId) {
        throw createHttpError(400, 'Thiếu mangaId');
    }

    if (!content) {
        throw createHttpError(400, 'Thiếu nội dung bình luận');
    }

    userService.createOrUpdateUser({ userId });

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        INSERT INTO comments (user_id, manga_id, chapter_id, content, updated_at)
        VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
    `);

    const result = stmt.run(userId, mangaId, chapterId, content);
    return getCommentById(result.lastInsertRowid);
}

function updateComment(commentId, payload = {}) {
    const existing = getCommentById(commentId);
    if (!existing) {
        throw createHttpError(404, 'Không tìm thấy bình luận');
    }

    const content = payload.content === undefined ? existing.content : normalizeText(payload.content);
    if (!content) {
        throw createHttpError(400, 'Thiếu nội dung bình luận');
    }

    const chapterId = hasOwn(payload, 'chapterId')
        ? normalizeNullable(payload.chapterId)
        : hasOwn(payload, 'chapter_id')
            ? normalizeNullable(payload.chapter_id)
            : existing.chapterId || null;

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        UPDATE comments
        SET chapter_id = ?, content = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
    `);

    stmt.run(chapterId, content, commentId);
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
            id,
            user_id,
            manga_id,
            chapter_id,
            content,
            created_at,
            updated_at
        FROM comments
        WHERE id = ?
    `);

    const row = stmt.get(commentId);
    return row ? mapComment(row) : null;
}

function mapComment(row) {
    return {
        id: row.id,
        userId: row.user_id,
        mangaId: row.manga_id,
        chapterId: row.chapter_id,
        content: row.content,
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
    getComments,
    createComment,
    updateComment,
    deleteComment
};
