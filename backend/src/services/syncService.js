const mangadexService = require('./mangadexService');
const databaseService = require('./databaseService');

const DEFAULT_SYNC_TOTAL = 200;
const DEFAULT_SYNC_LIMIT = 50;
const DEFAULT_SYNC_DELAY_MS = 500;
const DEFAULT_MAX_RETRIES = 2;
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
    const { total, limit, pages, offset, delayMs, maxRetries } = normalizePopularSyncOptions(options);
    const syncedMangas = [];
    const seenMangaIds = new Set();

    for (let page = 0; page < pages && syncedMangas.length < total; page++) {
        const remaining = total - syncedMangas.length;
        const pageLimit = Math.min(limit, remaining);
        const pageOffset = offset + page * limit;
        const startIndex = pageOffset + 1;
        const endIndex = pageOffset + pageLimit;

        console.log(`Syncing manga ${startIndex}-${endIndex}...`);
        const mangas = await fetchMangaPageWithRetry(pageLimit, pageOffset, maxRetries, delayMs);

        if (!mangas) {
            console.warn(`Skipped manga page at offset ${pageOffset}`);
            if (page < pages - 1) await delay(delayMs);
            continue;
        }

        if (!mangas.length) {
            break;
        }

        let savedCount = 0;
        for (const manga of mangas) {
            if (!manga.mangaId || seenMangaIds.has(manga.mangaId)) continue;

            try {
                seenMangaIds.add(manga.mangaId);
                databaseService.saveManga(manga);
                syncedMangas.push(manga);
                savedCount++;
            } catch (error) {
                console.error(`Failed to save manga ${manga.title || manga.mangaId}:`, error.message);
            }

            if (syncedMangas.length >= total) break;
        }

        console.log(`Saved ${savedCount} manga`);
        if (mangas.length < pageLimit) break;
        if (page < pages - 1 && syncedMangas.length < total) await delay(delayMs);
    }

    console.log(`Sync completed: ${syncedMangas.length} manga`);
    return syncedMangas;
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
    const pageSize = source.pageSize || source.page_size || source['page-size'] || source.limit;
    const requestedPages = parsePositiveInt(source.pages, null);
    const limit = clampNumber(pageSize, 1, MAX_SYNC_LIMIT, DEFAULT_SYNC_LIMIT);
    const totalFallback = requestedPages ? requestedPages * limit : DEFAULT_SYNC_TOTAL;
    const total = clampNumber(source.total || source.count, 1, MAX_SYNC_TOTAL, totalFallback);
    const pages = requestedPages
        ? clampNumber(requestedPages, 1, Math.ceil(MAX_SYNC_TOTAL / limit), Math.ceil(total / limit))
        : Math.ceil(total / limit);
    const offset = clampNumber(source.offset, 0, 10000, 0);
    const delayMs = clampNumber(source.delayMs || source.delay_ms || source.delay, 300, 700, DEFAULT_SYNC_DELAY_MS);
    const maxRetries = clampNumber(source.maxRetries || source.max_retries, 0, 5, DEFAULT_MAX_RETRIES);

    return { total, limit, pages, offset, delayMs, maxRetries };
}

async function fetchMangaPageWithRetry(limit, offset, maxRetries, delayMs) {
    for (let attempt = 0; attempt <= maxRetries; attempt++) {
        try {
            return await mangadexService.searchManga('', limit, offset);
        } catch (error) {
            if (error.statusCode === 429) {
                if (attempt >= maxRetries) {
                    console.warn(`Rate limited at offset ${offset}; retries exhausted.`);
                    return null;
                }

                const retryDelayMs = error.retryAfterMs || delayMs * (attempt + 1);
                console.warn(`Rate limited at offset ${offset}; retrying in ${retryDelayMs}ms.`);
                await delay(retryDelayMs);
                continue;
            }

            console.error(`Unable to sync MangaDex page at offset ${offset}:`, error.message);
            throw error;
        }
    }

    return null;
}

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
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
