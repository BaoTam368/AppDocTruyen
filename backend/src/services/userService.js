const databaseService = require('./databaseService');

function createOrUpdateUser(payload = {}) {
    const database = databaseService.getDatabase();
    const userId = normalizeText(payload.userId || payload.user_id || 'local_user');

    if (!userId) {
        throw createHttpError(400, 'Thiếu userId');
    }

    const stmt = database.prepare(`
        INSERT INTO users (user_id, display_name, email, avatar_url, updated_at)
        VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
        ON CONFLICT(user_id) DO UPDATE SET
            display_name = COALESCE(excluded.display_name, users.display_name),
            email = COALESCE(excluded.email, users.email),
            avatar_url = COALESCE(excluded.avatar_url, users.avatar_url),
            updated_at = CURRENT_TIMESTAMP
    `);

    stmt.run(
        userId,
        normalizeNullable(payload.displayName || payload.display_name),
        normalizeNullable(payload.email),
        normalizeNullable(payload.avatarUrl || payload.avatar_url)
    );

    return getUser(userId);
}

function getUser(userId) {
    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        SELECT
            id,
            user_id,
            display_name,
            email,
            avatar_url,
            created_at,
            updated_at
        FROM users
        WHERE user_id = ?
    `);

    const user = stmt.get(userId);
    return user ? mapUser(user) : null;
}

function updateUser(userId, payload = {}) {
    if (!getUser(userId)) {
        throw createHttpError(404, 'Không tìm thấy người dùng');
    }

    const database = databaseService.getDatabase();
    const stmt = database.prepare(`
        UPDATE users
        SET
            display_name = COALESCE(?, display_name),
            email = COALESCE(?, email),
            avatar_url = COALESCE(?, avatar_url),
            updated_at = CURRENT_TIMESTAMP
        WHERE user_id = ?
    `);

    stmt.run(
        normalizeNullable(payload.displayName || payload.display_name),
        normalizeNullable(payload.email),
        normalizeNullable(payload.avatarUrl || payload.avatar_url),
        userId
    );

    return getUser(userId);
}

function mapUser(row) {
    return {
        id: row.id,
        userId: row.user_id,
        displayName: row.display_name || '',
        email: row.email || '',
        avatarUrl: row.avatar_url || '',
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

function createHttpError(statusCode, message) {
    const error = new Error(message);
    error.statusCode = statusCode;
    return error;
}

module.exports = {
    createOrUpdateUser,
    getUser,
    updateUser
};
