const path = require('path');
const Database = require('better-sqlite3');

const dbPath = path.join(__dirname, '../../mangas.db');

try {
    const db = new Database(dbPath, { readonly: true });

    const postCount = db.prepare('SELECT COUNT(*) as count FROM posts').get();
    const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get();

    console.log(`\n Database: ${dbPath}`);
    console.log(`   - Posts: ${postCount.count}`);
    console.log(`   - Users: ${userCount.count}`);

    if (postCount.count > 0) {
        console.log(`\n 5 bài đăng gần nhất:`);
        const recentPosts = db.prepare(`
            SELECT p.id, p.user_id, u.display_name, 
                   SUBSTR(p.content, 1, 60) as preview,
                   p.like_count, p.created_at
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.user_id
            ORDER BY p.created_at DESC
            LIMIT 5
        `).all();

        for (const post of recentPosts) {
            console.log(`   [${post.id}] ${post.display_name || post.user_id}: "${post.preview}..." (❤️ ${post.like_count}) — ${post.created_at}`);
        }
    } else {
        console.log(`\n⚠  Bảng posts TRỐNG! Chạy: node backend/scripts/seedPosts.js`);
    }

    db.close();
    console.log('');
} catch (error) {
    console.error('Lỗi khi đọc database:', error.message);
    process.exit(1);
}
