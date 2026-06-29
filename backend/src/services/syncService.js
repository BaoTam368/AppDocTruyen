const mangadexService = require('./mangadexService');
const databaseService = require('./databaseService');

const DEFAULT_SYNC_TOTAL = 200;
const DEFAULT_SYNC_LIMIT = 100;
const MAX_SYNC_LIMIT = 100;
const MAX_SYNC_TOTAL = 500;

async function syncMangaFromMangaDex(mangaId) {
    try {
        const mangaDetail = await mangadexService.getMangaDetail(mangaId);
        databaseService.saveManga(mangaDetail);
        
        const chapters = await mangadexService.getMangaChapters(mangaId, { limit: 100 });
        chapters.forEach(chapter => {
            databaseService.saveChapter({
                ...chapter,
                mangaId
            });
        });
        
        return mangaDetail;
    } catch (error) {
        console.error(`Error syncing manga ${mangaId}:`, error.message);
        throw error;
    }
}

async function syncPopularMangas(options = {}) {
    const { total, limit, pages, offset } = normalizePopularSyncOptions(options);
    const syncedMangas = [];
    const seenMangaIds = new Set();

    try {
        for (let page = 0; page < pages && syncedMangas.length < total; page++) {
            const remaining = total - syncedMangas.length;
            const pageLimit = Math.min(limit, remaining);
            const pageOffset = offset + page * limit;
            const mangas = await mangadexService.searchManga('', pageLimit, pageOffset);

            if (!mangas.length) break;

            for (const manga of mangas) {
                if (!manga.mangaId || seenMangaIds.has(manga.mangaId)) continue;

                seenMangaIds.add(manga.mangaId);
                databaseService.saveManga(manga);
                syncedMangas.push(manga);
                console.log(`Synced manga summary: ${manga.title}`);

                if (syncedMangas.length >= total) break;
            }
        }

        return syncedMangas;
    } catch (error) {
        console.error('Error syncing popular mangas:', error.message);
        throw error;
    }
}

async function searchAndSync(query, options = {}) {
    try {
        const limit = clampNumber(options.limit, 1, MAX_SYNC_LIMIT, 20);
        const offset = clampNumber(options.offset, 0, 10000, 0);
        const mangas = await mangadexService.searchManga({ title: query, limit, offset });
        
        // Chỉ lưu summary (không gọi getMangaDetail từng cái) để search nhanh hơn
        for (const manga of mangas) {
            if (!manga.mangaId) continue;
            try {
                databaseService.saveManga(manga);
            } catch (error) {
                console.error(`Failed to save summary for ${manga.title}:`, error.message);
            }
        }
        
        return mangas;
    } catch (error) {
        console.error('Error searching and syncing:', error.message);
        throw error;
    }
}

function normalizePopularSyncOptions(options) {
    const source = typeof options === 'number' ? { total: options } : (options || {});
    const requestedPages = parsePositiveInt(source.pages, null);
    const limit = clampNumber(source.limit, 1, MAX_SYNC_LIMIT, DEFAULT_SYNC_LIMIT);
    const totalFallback = requestedPages ? requestedPages * limit : DEFAULT_SYNC_TOTAL;
    const total = clampNumber(source.total || source.count, 1, MAX_SYNC_TOTAL, totalFallback);
    const pages = requestedPages
        ? clampNumber(requestedPages, 1, Math.ceil(MAX_SYNC_TOTAL / limit), Math.ceil(total / limit))
        : Math.ceil(total / limit);
    const offset = clampNumber(source.offset, 0, 10000, 0);

    return { total, limit, pages, offset };
}

function parsePositiveInt(value, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue) || numberValue <= 0) return fallback;
    return numberValue;
}

function clampNumber(value, min, max, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue)) return fallback;
    return Math.max(min, Math.min(max, numberValue));
}

module.exports = {
    syncMangaFromMangaDex,
    syncPopularMangas,
    searchAndSync
};
