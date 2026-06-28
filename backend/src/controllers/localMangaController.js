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
        const q = req.query.q || req.query.title;
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
                message: 'Manga not found in database'
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
            message: 'Manga synced successfully'
        });
    } catch (error) {
        next(error);
    }
}

async function syncPopular(req, res, next) {
    try {
        const mangas = await syncService.syncPopularMangas(req.query);

        res.json({
            success: true,
            data: mangas,
            message: `Synced ${mangas.length} popular manga`
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
                message: 'Missing search keyword'
            });
        }

        const mangas = await syncService.searchAndSync(q, req.query);

        res.json({
            success: true,
            data: mangas,
            message: `Found and synced ${mangas.length} manga`
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
