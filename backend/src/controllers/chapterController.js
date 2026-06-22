const mangadexService = require('../services/mangadexService');

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
    getChapterPages
};
