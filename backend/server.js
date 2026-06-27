require('dotenv').config();

const cors = require('cors');
const express = require('express');
const path = require('path');

const chapterRoutes = require('./src/routes/chapterRoutes');
const commentRoutes = require('./src/routes/commentRoutes');
const groupRoutes = require('./src/routes/groupRoutes');
const mangaRoutes = require('./src/routes/mangaRoutes');
const localMangaRoutes = require('./src/routes/localMangaRoutes');
const postRoutes = require('./src/routes/postRoutes');
const userRoutes = require('./src/routes/userRoutes');

const app = express();
const port = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

app.get('/api/health', (req, res) => {
    res.json({
        success: true,
        message: 'Dịch vụ Node.js MangaDex đang chạy'
    });
});

app.use('/api/manga', mangaRoutes);
app.use('/api/chapter', chapterRoutes);
app.use('/api/local-manga', localMangaRoutes);
app.use('/api/users', userRoutes);
app.use('/api/comments', commentRoutes);
app.use('/api/groups', groupRoutes);
app.use('/api/posts', postRoutes);

app.use((req, res) => {
    res.status(404).json({
        success: false,
        message: 'Không tìm thấy endpoint'
    });
});

app.use((err, req, res, next) => {
    console.error("LỖI :", err);
    const statusCode = err.statusCode || 500;
    res.status(statusCode).json({
        success: false,
        message: err.message || 'Server lỗi'
    });
});

app.listen(port, () => {
    console.log(`Node.js MangaDex service is running at http://localhost:${port}`);
});

