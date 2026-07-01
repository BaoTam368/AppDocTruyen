const mangadexService = require('../services/mangadexService');
const databaseService = require('../services/databaseService');

async function getMangaList(req, res, next) {
    try {
        const title = req.query.title || req.query.q;
        const { limit, offset, status, tag, sort } = req.query;
        const options = { limit, offset, status, tag, sort };
        const data = title && String(title).trim()
            ? databaseService.searchMangas(String(title).trim(), options)
            : databaseService.getAllMangas(options);

        res.json({
            success: true,
            data,
            message: data.length ? 'OK' : 'No cached manga available. Please sync first.'
        });
    } catch (error) {
        next(error);
    }
}

async function getMangaDetail(req, res, next) {
    try {
        const cachedManga = databaseService.getMangaById(req.params.mangaId);
        if (cachedManga) {
            return res.json({
                success: true,
                data: cachedManga,
                source: 'cache'
            });
        }

        const data = await mangadexService.getMangaDetail(req.params.mangaId);
        databaseService.saveManga(data);

        res.json({
            success: true,
            data,
            source: 'mangadex'
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
        const cachedChapters = databaseService.getMangaChapters(req.params.mangaId);
        if (cachedChapters.length) {
            return res.json({
                success: true,
                data: cachedChapters,
                source: 'cache'
            });
        }

        const data = await mangadexService.getMangaChapters(req.params.mangaId, {
            limit: req.query.limit,
            offset: req.query.offset,
            language: req.query.language || req.query.translatedLanguage
        });

        data.forEach((chapter) => {
            if (chapter.chapterId) {
                databaseService.saveChapter({ ...chapter, mangaId: req.params.mangaId });
            }
        });

        res.json({
            success: true,
            data,
            source: 'mangadex'
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