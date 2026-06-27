const databaseService = require('./databaseService');
const userService = require('./userService');

function getComments({  limit = 50, offset = 0 } = {}) {
    const database = databaseService.getDatabase();
    const filters = [];
    const params = [];

    const whereClause = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
    const stmt = database.prepare(`
        SELECT
            c.id,
            c.user_id,
            c.content,
            c.created_at,
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
    const userId = normalizeText(payload.userId || payload.user_id);
    const content = normalizeText(payload.content);

    if (!content) {
        throw createHttpError(400, 'Thiếu nội dung bình luận');
    }

    userService.createOrUpdateUser({ userId });

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        INSERT INTO comments (user_id, content)
        VALUES (?, ?)
    `);

    const result = stmt.run(userId, content);
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

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        UPDATE comments
        SET content = ?
        WHERE id = ?
    `);

    stmt.run( content, commentId);
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
            c.user_id,
            c.content,
            c.created_at,
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
    return {
        id: row.id,
        userId: row.user_id,
        content: row.content,
        created_at: row.created_at,
        display_name: row.display_name,
        avatar_url: row.avatar_url
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
    getCommentById,
    updateComment,
    deleteComment
};
