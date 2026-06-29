const databaseService = require('./databaseService');
const userService = require('./userService');

function getPosts({ userId, limit = 50, offset = 0 } = {}) {
    const database = databaseService.getDatabase();
    const filters = [];
    const params = [];

    if (userId) {
        filters.push('p.user_id = ?');
        params.push(userId);
    }

    const whereClause = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
    const stmt = database.prepare(`
            SELECT
                p.id,
                p.user_id,
                u.display_name,
                u.avatar_url,
                p.content,
                p.image_url,
                p.like_count,
                p.created_at
            FROM posts p
            LEFT JOIN users u
            ON p.user_id = u.user_id
        ${whereClause}
        ORDER BY p.created_at DESC
        LIMIT ? OFFSET ?
    `);

    const rows = stmt.all(...params, clampNumber(limit, 1, 100, 50), clampNumber(offset, 0, 10000, 0));
    return rows.map(mapPost);
}

function getPostById(postId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        SELECT
             p.id,
             p.user_id,
             u.display_name,
             u.avatar_url,
             p.content,
             p.image_url,
             p.like_count,
             p.created_at
        FROM posts p
        LEFT JOIN users u
        ON p.user_id = u.user_id
        WHERE p.id = ?
    `);

    const row = stmt.get(postId);
    return row ? mapPost(row) : null;
}

function createPost(payload = {}) {
    const userId = normalizeText(payload.userId || payload.user_id);
    const content = normalizeText(payload.content);
    const imageUrl = normalizeNullable(payload.imageUrl || payload.image_url);

    if (!userId) {
        throw createHttpError(400, 'User is required.');
    }

    if (!content) {
        throw createHttpError(400, 'Missing post content');
    }

    userService.createOrUpdateUser({ userId });

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`

            INSERT INTO posts
            (user_id, content, image_url)
            VALUES (?, ?, ?)
    `);

    const result = stmt.run(userId, content, imageUrl);
    return getPostById(result.lastInsertRowid);
}

function updatePost(postId, payload = {}) {
    const existing = getPostById(postId);
    if (!existing) {
        throw createHttpError(404, 'Post not found');
    }

    const content = hasOwn(payload, 'content') ? normalizeText(payload.content) : existing.content;
    const imageUrl = hasOwn(payload, 'imageUrl')
        ? normalizeNullable(payload.imageUrl)
        : hasOwn(payload, 'image_url')
            ? normalizeNullable(payload.image_url)
            : existing.imageUrl;

    if (!content) {
        throw createHttpError(400, 'Missing post content');
    }

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`

        UPDATE posts
        SET
            content = ?,
            image_url = ?
        WHERE id = ?

    `);

    stmt.run( content, imageUrl, postId);
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
        user_id: row.user_id,

        display_name: row.display_name,
        displayName: row.display_name,

        avatarUrl: row.avatar_url,
        avatar_url: row.avatar_url,

        content: row.content,

        imageUrl: row.image_url,
        image_url: row.image_url,

        likeCount: row.like_count,
        like_count: row.like_count,

        createdAt: row.created_at,
        created_at: row.created_at
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

function toggleLikePost(postId, userId) {
    const database = databaseService.getDatabase();

    const checkLike = database.prepare('SELECT 1 FROM post_likes WHERE user_id = ? AND post_id = ?').get(userId, postId);

    if (checkLike) {
        database.prepare('DELETE FROM post_likes WHERE user_id = ? AND post_id = ?').run(userId, postId);
        database.prepare('UPDATE posts SET like_count = MAX(0, like_count - 1) WHERE id = ?').run(postId);

        return { liked: false, likeCount: getLikeCount(postId) };
    } else {
        database.prepare('INSERT INTO post_likes (user_id, post_id) VALUES (?, ?)').run(userId, postId);
        database.prepare('UPDATE posts SET like_count = like_count + 1 WHERE id = ?').run(postId);

        return { liked: true, likeCount: getLikeCount(postId) };
    }
}

function getLikeCount(postId) {
    const database = databaseService.getDatabase();
    const row = database.prepare('SELECT like_count FROM posts WHERE id = ?').get(postId);
    return row ? row.like_count : 0;
}

module.exports = {
    getPosts,
    getPostById,
    createPost,
    updatePost,
    deletePost,
    toggleLikePost,
    getLikeCount
};
