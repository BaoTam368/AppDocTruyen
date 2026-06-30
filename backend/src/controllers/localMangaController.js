const databaseService = require('../services/databaseService');
const syncService = require('../services/syncService');

async function getLocalMangaList(req, res, next) {
    try {
        const { limit, offset, status, tag, sort } = req.query;
        const data = databaseService.getAllMangas({ limit, offset, status, tag, sort });

        res.json({
            success: true,
            data,
            message: data.length ? 'OK' : 'No cached manga available. Please sync first.'
        });
    } catch (error) {
        next(error);
    }
}

async function searchLocalMangas(req, res, next) {
    try {
        const q = req.query.q || req.query.title;
        const { limit, offset, status, tag, sort } = req.query;
        
        if (!q || !String(q).trim()) {
            return res.json({
                success: true,
                data: [],
                message: 'Search manga...'
            });
        }

        const data = databaseService.searchMangas(String(q).trim(), { limit, offset, status, tag, sort });

        res.json({
            success: true,
            data,
            message: data.length ? 'OK' : 'No cached manga found.'
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
        const mangas = await syncService.syncPopularMangas({ ...req.query, ...req.body });

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
        const q = req.query.q || req.query.title;
        if (!q || !String(q).trim()) {
            return res.status(400).json({
                success: false,
                message: 'Missing search keyword'
            });
        }

        const mangas = await syncService.searchAndSync(String(q).trim(), req.query);

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
