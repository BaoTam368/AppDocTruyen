const express = require('express');
const mangadexService = require('../services/mangadexService');

const router = express.Router();

router.get('/', async (req, res, next) => {
    try {
        const data = await mangadexService.getGroups({
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
});

router.get('/:groupId', async (req, res, next) => {
    try {
        const data = await mangadexService.getGroupDetail(req.params.groupId);

        res.json({
            success: true,
            data
        });
    } catch (error) {
        next(error);
    }
});

module.exports = router;
