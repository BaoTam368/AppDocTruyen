const mangadexService = require('../services/mangadexService');

async function getMangaList(req, res, next) {
    try {
        const data = await mangadexService.searchManga({
            title: req.query.title,
            limit: req.query.limit,
            offset: req.query.offset
        });

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function getMangaDetail(req, res, next) {
    try {
        const data = await mangadexService.getMangaDetail(req.params.mangaId);

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function getMangaCovers(req, res, next) {
    try {
        const data = await mangadexService.getMangaCovers(req.params.mangaId, {
            limit: req.query.limit,
            offset: req.query.offset
        });

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function getMangaChapters(req, res, next) {
    try {
        const data = await mangadexService.getMangaChapters(req.params.mangaId, {
            limit: req.query.limit,
            offset: req.query.offset,
            language: req.query.language || req.query.translatedLanguage
        });

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

async function getChapterPages(req, res, next) {
    try {
        const data = await mangadexService.getChapterPages(req.params.chapterId);

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getMangaList,
    getMangaDetail,
    getMangaCovers,
    getMangaChapters,
    getChapterPages
};