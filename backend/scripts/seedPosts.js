/**
 * Seed Script: Tạo dữ liệu bài đăng mẫu cho trang Community
 * 
 * Cách chạy: node backend/scripts/seedPosts.js
 * 
 * Script này chỉ INSERT thêm dữ liệu, KHÔNG xóa/truncate bảng.
 * An toàn để chạy nhiều lần (mỗi lần thêm 15 posts mới).
 */

const path = require('path');
const Database = require('better-sqlite3');

const dbPath = path.join(__dirname, '../../mangas.db');
const db = new Database(dbPath);
db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

// ═══════════════════════════════
// Fake users cho seed data
// ═══════════════════════════════
const seedUsers = [
    { userId: 'seed_user_01', displayName: 'Minh Tuấn', email: 'minhtuan@example.com', avatarUrl: null },
    { userId: 'seed_user_02', displayName: 'Sakura_Fan', email: 'sakura@example.com', avatarUrl: null },
    { userId: 'seed_user_03', displayName: 'Trần Hải Yến', email: 'haiyen@example.com', avatarUrl: null },
    { userId: 'seed_user_04', displayName: 'manga_otaku99', email: 'otaku99@example.com', avatarUrl: null },
    { userId: 'seed_user_05', displayName: 'Nguyễn Đức Anh', email: 'ducanh@example.com', avatarUrl: null },
    { userId: 'seed_user_06', displayName: 'Lê Thị Mai', email: 'lemai@example.com', avatarUrl: null },
    { userId: 'seed_user_07', displayName: 'DarkKnight_VN', email: 'darkknight@example.com', avatarUrl: null },
    { userId: 'seed_user_08', displayName: 'Phạm Quốc Bảo', email: 'quocbao@example.com', avatarUrl: null },
    { userId: 'seed_user_09', displayName: 'Yuki_chan', email: 'yuki@example.com', avatarUrl: null },
    { userId: 'seed_user_10', displayName: 'Hoàng Văn Nam', email: 'hvnam@example.com', avatarUrl: null },
];

// ═══════════════════════════════
// Bài đăng mẫu — đa dạng chủ đề manga/truyện tranh
// ═══════════════════════════════
const seedPosts = [
    {
        userId: 'seed_user_01',
        content: 'One Piece chapter mới ra rồi anh em ơi! Oda sensei lại twist điên quá, Gear 5 Luffy đánh nhau với Gorosei mà đọc muốn rụng tim 🔥🔥🔥',
        likeCount: 47,
        daysAgo: 0.2,
    },
    {
        userId: 'seed_user_02',
        content: 'Có ai đọc "Frieren: Beyond Journey\'s End" chưa? Mình mới bắt đầu đọc mà không ngờ nó hay đến vậy. Cảm xúc lắng đọng, artwork đẹp tinh tế 💎',
        likeCount: 32,
        daysAgo: 0.5,
    },
    {
        userId: 'seed_user_03',
        content: 'Xin gợi ý manga thể loại romance school life hay đi mọi người! Mình đọc hết Kaguya-sama rồi, giờ trống vắng quá 😭',
        likeCount: 18,
        daysAgo: 1,
    },
    {
        userId: 'seed_user_04',
        content: '⚠️ SPOILER ALERT — Jujutsu Kaisen ⚠️\nKhông thể tin Gojo thực sự... các bạn đọc chap mới nhất chưa? Akutami sensei quá tàn nhẫn với fan 😱',
        likeCount: 63,
        daysAgo: 1.3,
    },
    {
        userId: 'seed_user_05',
        content: 'Solo Leveling manhwa kết thúc rồi nhưng vẫn nhớ cảm giác đọc arc Jeju Island lần đầu. Sung Jin-Woo đỉnh nhất! 🗡️ Ai đọc side story chưa?',
        likeCount: 41,
        daysAgo: 1.8,
    },
    {
        userId: 'seed_user_06',
        content: 'Mọi người thấy nhóm dịch nào dịch manga Nhật sang tiếng Việt nhanh và chất lượng nhất? Mình thấy có vài nhóm dịch rất nhanh nhưng lỗi chính tả nhiều quá 😅',
        likeCount: 22,
        daysAgo: 2.1,
    },
    {
        userId: 'seed_user_07',
        content: 'Vừa đọc xong Chainsaw Man Part 2. Fujimoto vẫn là thiên tài điên rồ như thường lệ. Không ai đoán được ổng sẽ làm gì tiếp theo 🤯',
        likeCount: 35,
        daysAgo: 2.5,
    },
    {
        userId: 'seed_user_08',
        content: 'Top 5 manga isekai hay nhất theo mình:\n1. Mushoku Tensei\n2. Re:Zero\n3. Overlord\n4. That Time I Got Reincarnated as a Slime\n5. The Rising of the Shield Hero\nMọi người bổ sung thêm nha!',
        likeCount: 28,
        daysAgo: 3,
    },
    {
        userId: 'seed_user_09',
        content: 'Spy x Family dễ thương quá trời 🥜❤️ Anya là nhân vật cute nhất mà mình từng thấy trong manga. Waku waku~',
        likeCount: 55,
        daysAgo: 3.4,
    },
    {
        userId: 'seed_user_10',
        content: 'Chờ chapter mới của Kaiju No. 8 mà lâu quá... tác giả nghỉ 2 tuần rồi 😩 Có ai biết khi nào ra chap mới không?',
        likeCount: 15,
        daysAgo: 4,
    },
    {
        userId: 'seed_user_01',
        content: 'Mới tìm được app đọc truyện này, giao diện sạch sẽ dễ dùng ghê. Anh em nào có truyện hay recommend thì comment bên dưới nha 📚',
        likeCount: 39,
        daysAgo: 4.5,
    },
    {
        userId: 'seed_user_03',
        content: 'Blue Lock đang ở arc hay nhất! Isagi phát triển nhanh quá, từ một cầu thủ bình thường giờ đã thành top striker. Anime season 2 cũng sắp chiếu rồi 🔵⚽',
        likeCount: 26,
        daysAgo: 5,
    },
    {
        userId: 'seed_user_05',
        content: 'Vinland Saga kết thúc rồi 😢 Cảm ơn Makoto Yukimura sensei vì một tác phẩm tuyệt vời. Thorfinn đã tìm được Vinland của riêng mình 🌊⚔️',
        likeCount: 44,
        daysAgo: 5.5,
    },
    {
        userId: 'seed_user_09',
        content: 'Ai đang đọc Dandadan không? Art style quá đỉnh luôn, action scenes mà cứ như xem anime vậy. Manga đáng đọc nhất 2025 theo mình! ✨',
        likeCount: 30,
        daysAgo: 6,
    },
    {
        userId: 'seed_user_07',
        content: 'Mới re-read lại Naruto từ đầu. Đọc lại vẫn xúc động ở arc Pain Invasion và trận đánh cuối Naruto vs Sasuke. Tuổi thơ mãi đỉnh 🍥',
        likeCount: 52,
        daysAgo: 6.5,
    },
];

// ═══════════════════════════════
// Insert seed data
// ═══════════════════════════════

// Ensure tables exist
db.exec(`
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id TEXT NOT NULL UNIQUE,
        display_name TEXT,
        email TEXT,
        avatar_url TEXT,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
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
`);

const insertUser = db.prepare(`
    INSERT INTO users (user_id, display_name, email, avatar_url, updated_at)
    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
    ON CONFLICT(user_id) DO UPDATE SET
        display_name = COALESCE(excluded.display_name, users.display_name),
        email = COALESCE(excluded.email, users.email),
        avatar_url = COALESCE(excluded.avatar_url, users.avatar_url),
        updated_at = CURRENT_TIMESTAMP
`);

const insertPost = db.prepare(`
    INSERT INTO posts (user_id, content, image_url, like_count, created_at)
    VALUES (?, ?, NULL, ?, datetime('now', '+7 hours', ?))
`);

const insertMany = db.transaction(() => {
    // Insert users
    let usersInserted = 0;
    for (const user of seedUsers) {
        insertUser.run(user.userId, user.displayName, user.email, user.avatarUrl);
        usersInserted++;
    }
    console.log(`✅ Đã upsert ${usersInserted} seed users`);

    // Insert posts
    let postsInserted = 0;
    for (const post of seedPosts) {
        const daysOffset = `-${post.daysAgo} days`;
        insertPost.run(post.userId, post.content, post.likeCount, daysOffset);
        postsInserted++;
    }
    console.log(`✅ Đã insert ${postsInserted} seed posts`);
});

try {
    insertMany();

    // Verify
    const postCount = db.prepare('SELECT COUNT(*) as count FROM posts').get();
    const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get();
    console.log(`\n📊 Tổng cộng trong database:`);
    console.log(`   - Users: ${userCount.count}`);
    console.log(`   - Posts: ${postCount.count}`);
    console.log(`\n🎉 Seed data hoàn tất! Mở app và vào tab Community để xem.`);
} catch (error) {
    console.error('❌ Lỗi khi seed data:', error.message);
    process.exit(1);
} finally {
    db.close();
}
