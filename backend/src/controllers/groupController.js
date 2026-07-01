const mangadexService = require('../services/mangadexService');
const databaseService = require('../services/databaseService');

async function getGroups(req, res, next) {
    try {
        const data = await mangadexService.getGroups({
            limit: req.query.limit,
            offset: req.query.offset
        });

        const enriched = await enrichGroupsWithMangaCount(data);

        res.json({
            success: true,
            data: enriched,
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

        const enriched = await enrichGroupsWithMangaCount(data);

        res.json({
            success: true,
            data: enriched,
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

async function getGroupDetail(req, res, next) {
    try {
        const data = await mangadexService.getGroupDetail(req.params.groupId);

        const mangaCount = await resolveGroupMangaCount(data.groupId);
        data.mangaCount = mangaCount;
        data.comicCount = mangaCount;

        res.json({
            success: true,
            data,
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

async function getGroupMangaCount(req, res, next) {
    try {
        const groupId = req.params.groupId;
        if (!groupId || !groupId.trim()) {
            return res.status(400).json({
                success: false,
                message: 'Missing groupId'
            });
        }

        const mangaCount = await resolveGroupMangaCount(groupId.trim());

        res.json({
            success: true,
            data: { groupId: groupId.trim(), mangaCount },
            message: 'OK'
        });
    } catch (error) {
        next(error);
    }
}

async function enrichGroupsWithMangaCount(groups) {
    if (!Array.isArray(groups) || groups.length === 0) return groups;

    const enrichPromises = groups.map(async (group) => {
        try {
            const mangaCount = await resolveGroupMangaCount(group.groupId);
            return { ...group, mangaCount, comicCount: mangaCount };
        } catch (error) {
            console.warn(`Failed to enrich manga count for group ${group.groupId}:`, error.message);
            return group;
        }
    });

    return Promise.all(enrichPromises);
}

async function resolveGroupMangaCount(groupId) {
    if (!groupId) return 0;

    const cached = databaseService.getCachedGroupMangaCount(groupId);
    if (cached !== null) {
        return cached;
    }

    try {
        const mangaCount = await mangadexService.getGroupMangaCount(groupId);
        databaseService.setCachedGroupMangaCount(groupId, mangaCount);
        return mangaCount;
    } catch (error) {
        console.warn(`Unable to fetch manga count for group ${groupId}:`, error.message);
        return 0;
    }
}

module.exports = {
    getGroups,
    searchGroups,
    getGroupDetail,
    getGroupMangaCount
};
