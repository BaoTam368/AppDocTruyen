const databaseService = require('../services/databaseService');
const syncService = require('../services/syncService');

async function getLocalMangaList(req, res, next) {
    try {
        const { limit, offset } = req.query;
        const data = databaseService.getAllMangas({ limit, offset });

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function searchLocalMangas(req, res, next) {
    try {
        const { q } = req.query;
        const { limit, offset } = req.query;
        
        if (!q) {
            return res.json({
                success: true,
                data: []
            });
        }

        const data = databaseService.searchMangas(q, { limit, offset });

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function getLocalMangaDetail(req, res, next) {
    try {
        const { mangaId } = req.params;
        const manga = databaseService.getMangaById(mangaId);
        
        if (!manga) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy truyện trong database'
            });
        }

        res.json({
            success: true,
            data: manga
        });
    } catch (error) {
        next(error);
    }
}

async function getLocalMangaChapters(req, res, next) {
    try {
        const { mangaId } = req.params;
        const chapters = databaseService.getMangaChapters(mangaId);

        res.json({
            success: true,
            data: chapters
        });
    } catch (error) {
        next(error);
    }
}

async function syncFromMangaDex(req, res, next) {
    try {
        const { mangaId } = req.params;
        const manga = await syncService.syncMangaFromMangaDex(mangaId);

        res.json({
            success: true,
            data: manga,
            message: 'Đã đồng bộ truyện thành công'
        });
    } catch (error) {
        next(error);
    }
}

async function syncPopular(req, res, next) {
    try {
        const { count } = req.query;
        const mangas = await syncService.syncPopularMangas(count ? parseInt(count) : 20);

        res.json({
            success: true,
            data: mangas,
            message: `Đã đồng bộ ${mangas.length} truyện phổ biến`
        });
    } catch (error) {
        next(error);
    }
}

async function searchAndSync(req, res, next) {
    try {
        const { q } = req.query;
        if (!q) {
            return res.status(400).json({
                success: false,
                message: 'Thiếu từ khóa tìm kiếm'
            });
        }

        const mangas = await syncService.searchAndSync(q);

        res.json({
            success: true,
            data: mangas,
            message: `Đã tìm và đồng bộ ${mangas.length} truyện`
        });
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getLocalMangaList,
    searchLocalMangas,
    getLocalMangaDetail,
    getLocalMangaChapters,
    syncFromMangaDex,
    syncPopular,
    searchAndSync
};
