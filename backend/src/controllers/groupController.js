const mangadexService = require('../services/mangadexService');

async function getGroups(req, res, next) {
    try {
        const data = await mangadexService.getGroups({
            limit: req.query.limit,
            offset: req.query.offset
        });

        res.json({
            success: true,
            data,
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

async function searchGroups(req, res, next) {
    try {
        const data = await mangadexService.searchGroups(req.query.name, {
            limit: req.query.limit,
            offset: req.query.offset
        });

        res.json({
            success: true,
            data,
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

async function getGroupDetail(req, res, next) {
    try {
        const data = await mangadexService.getGroupDetail(req.params.groupId);

        res.json({
            success: true,
            data,
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getGroups,
    searchGroups,
    getGroupDetail
};
