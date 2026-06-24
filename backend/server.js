require('dotenv').config();

const cors = require('cors');
const express = require('express');

const mangaRoutes = require('./src/routes/mangaRoutes');
const localMangaRoutes = require('./src/routes/localMangaRoutes');

const app = express();
const port = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

app.get('/api/health', (req, res) => {
    res.json({
        success: true,
        message: 'Node.js MangaDex service is running'
    });
});

app.use('/api/manga', mangaRoutes);
app.use('/api/local-manga', localMangaRoutes);

app.use((req, res) => {
    res.status(404).json({
        success: false,
        message: 'Không tìm thấy endpoint'
    });
});

app.use((err, req, res, next) => {
    const statusCode = err.statusCode || 500;
    res.status(statusCode).json({
        success: false,
        message: err.message || 'Server lỗi'
    });
});

app.listen(port, () => {
    console.log(`Node.js MangaDex service is running at http://localhost:${port}`);
});
