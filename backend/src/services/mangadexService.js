const axios = require('axios');

const MANGADEX_BASE_URL = trimTrailingSlash(process.env.MANGADEX_BASE_URL || 'https://api.mangadex.org');
const MANGADEX_UPLOADS_BASE_URL = trimTrailingSlash(process.env.MANGADEX_UPLOADS_BASE_URL || 'https://uploads.mangadex.org');
const DEFAULT_TRANSLATED_LANGUAGE = process.env.DEFAULT_TRANSLATED_LANGUAGE || 'en';
const configuredTimeout = Number.parseInt(process.env.REQUEST_TIMEOUT_MS || '10000', 10);
const REQUEST_TIMEOUT_MS = Number.isNaN(configuredTimeout) ? 10000 : configuredTimeout;
const MANGADEX_MAX_LIMIT = 100;

const mangadexClient = axios.create({
    baseURL: MANGADEX_BASE_URL,
    timeout: REQUEST_TIMEOUT_MS,
    headers: {
        'User-Agent': 'AppDocTruyenStudentDemo/1.0'
    }
});

async function searchManga(titleOrOptions = '', limit = 20, offset = 0) {
    const options = typeof titleOrOptions === 'object' && titleOrOptions !== null
        ? titleOrOptions
        : {title: titleOrOptions, limit, offset};

    const params = new URLSearchParams();
    params.set('limit', clampNumber(options.limit, 1, MANGADEX_MAX_LIMIT, 20));
    params.set('offset', clampNumber(options.offset, 0, 10000, 0));
    params.append('includes[]', 'cover_art');
    params.append('contentRating[]', 'safe');
    params.append('contentRating[]', 'suggestive');
    params.set('order[latestUploadedChapter]', 'desc');

    const searchTitle = options.title || '';
    if (searchTitle && searchTitle.trim()) {
        params.set('title', searchTitle.trim());
    }

    try {
        const response = await mangadexClient.get('/manga', {params});
        return (response.data.data || []).map(mapMangaSummary);
    } catch (error) {
        throw normalizeMangaDexError(error, 'Unable to load manga list from MangaDex');
    }
}

async function getMangaDetail(mangaId) {
    if (!mangaId || !mangaId.trim()) {
        throw createHttpError(400, 'Missing mangaId');
    }

    const params = new URLSearchParams();
    params.append('includes[]', 'cover_art');

    try {
        const response = await mangadexClient.get(`/manga/${mangaId}`, {params});
        if (!response.data.data) {
            throw createHttpError(404, 'Manga not found');
        }

        let statsData = {};
        try {
            const statsRes = await mangadexClient.get(`/statistics/manga/${mangaId}`);
            statsData = statsRes.data.statistics[mangaId] || {};
        } catch (statsError) {
            console.warn(`Unable to load manga statistics for ${mangaId}:`, statsError.message);
        }
        return mapMangaDetail(response.data.data, statsData);
    } catch (error) {
        if (error.statusCode) throw error;
        throw normalizeMangaDexError(error, 'Unable to load manga details from MangaDex');
    }
}

async function getMangaCovers(mangaId, {limit = 100, offset = 0} = {}) {
    if (!mangaId || !mangaId.trim()) {
        throw createHttpError(400, 'Missing mangaId');
    }

    const normalizedMangaId = mangaId.trim();
    const params = new URLSearchParams();
    params.append('manga[]', normalizedMangaId);
    params.set('limit', clampNumber(limit, 1, MANGADEX_MAX_LIMIT, 100));
    params.set('offset', clampNumber(offset, 0, 10000, 0));
    params.set('order[createdAt]', 'desc');

    try {
        const response = await mangadexClient.get('/cover', {params});
        return (response.data.data || []).map((item) => mapCover(item, normalizedMangaId));
    } catch (error) {
        throw normalizeMangaDexError(error, 'Unable to load cover list from MangaDex');
    }
}

async function getMangaChapters(mangaId, {limit = 100, offset = 0, language = DEFAULT_TRANSLATED_LANGUAGE} = {}) {
    if (!mangaId || !mangaId.trim()) {
        throw createHttpError(400, 'Missing mangaId');
    }

    const params = new URLSearchParams();
    const selectedLanguage = language && String(language).trim()
        ? String(language).trim()
        : DEFAULT_TRANSLATED_LANGUAGE;

    params.set('limit', clampNumber(limit, 1, MANGADEX_MAX_LIMIT, 100));
    params.set('offset', clampNumber(offset, 0, 10000, 0));
    params.append('translatedLanguage[]', selectedLanguage);
    params.set('order[chapter]', 'asc');

    try {
        const response = await mangadexClient.get(`/manga/${mangaId}/feed`, {params});
        return sortChapters((response.data.data || []).map((item) => mapChapterSummary(item, mangaId)));
    } catch (error) {
        throw normalizeMangaDexError(error, 'Unable to load chapter list from MangaDex');
    }
}

async function getChapterPages(chapterId) {
    if (!chapterId || !chapterId.trim()) {
        throw createHttpError(400, 'Missing chapterId');
    }

    try {
        const response = await mangadexClient.get(`/at-home/server/${chapterId}`);
        const chapter = response.data.chapter;
        if (!chapter || !chapter.hash || !Array.isArray(chapter.data)) {
            throw createHttpError(404, 'No images found for this chapter');
        }

        return {
            chapterId,
            pages: chapter.data.map((fileName) =>
                `${response.data.baseUrl}/data/${chapter.hash}/${fileName}`
            )
        };
    } catch (error) {
        if (error.statusCode) throw error;
        throw normalizeMangaDexError(error, 'Unable to load reader pages from MangaDex');
    }
}

async function getGroups({limit = 50, offset = 0, name = ''} = {}) {
    const params = new URLSearchParams();
    params.set('limit', clampNumber(limit, 1, MANGADEX_MAX_LIMIT, 50));
    params.set('offset', clampNumber(offset, 0, 10000, 0));

    if (name && String(name).trim()) {
        params.set('name', String(name).trim());
    }

    try {
        const response = await mangadexClient.get('/group', {params});
        return (response.data.data || []).map(mapGroupSummary);
    } catch (error) {
        throw normalizeMangaDexError(error, 'Unable to load scanlation groups from MangaDex');
    }
}

function searchGroups(name, {limit = 50, offset = 0} = {}) {
    return getGroups({name, limit, offset});
}

async function getGroupDetail(groupId) {
    if (!groupId || !groupId.trim()) {
        throw createHttpError(400, 'Missing groupId');
    }

    try {
        const response = await mangadexClient.get(`/group/${groupId}`);
        if (!response.data.data) {
            throw createHttpError(404, 'Scanlation group not found');
        }
        return mapGroupSummary(response.data.data);
    } catch (error) {
        if (error.statusCode) throw error;
        throw normalizeMangaDexError(error, 'Unable to load scanlation group details from MangaDex');
    }
}

async function getGroupMangaCount(groupId) {
    if (!groupId || !groupId.trim()) {
        throw createHttpError(400, 'Missing groupId');
    }

    const uniqueMangaIds = new Set();
    let offset = 0;
    const limit = 100;
    const maxPages = 5;

    for (let page = 0; page < maxPages; page++) {
        const params = new URLSearchParams();
        params.append('groups[]', groupId.trim());
        params.set('limit', String(limit));
        params.set('offset', String(offset));
        params.set('order[publishAt]', 'desc');

        try {
            const response = await mangadexClient.get('/chapter', {params});
            const chapters = response.data.data || [];

            for (const chapter of chapters) {
                const mangaRel = (chapter.relationships || []).find((r) => r.type === 'manga');
                if (mangaRel && mangaRel.id) {
                    uniqueMangaIds.add(mangaRel.id);
                }
            }

            const total = response.data.total || 0;
            offset += limit;
            if (offset >= total || chapters.length < limit) {
                break;
            }
        } catch (error) {
            if (page === 0) {
                throw normalizeMangaDexError(error, 'Unable to count manga for this group');
            }
            break;
        }
    }

    return uniqueMangaIds.size;
}

function mapMangaSummary(item) {
    const attributes = item.attributes || {};
    const coverFileName = getCoverFileName(item);
    return {
        mangaId: item.id,
        title: pickLocalizedText(attributes.title) || 'Untitled',
        description: shortenText(pickLocalizedText(attributes.description), 180),
        coverUrl: buildCoverUrl(item.id, coverFileName),
        status: attributes.status || '',
        year: attributes.year || null,
        tags: mapTags(attributes.tags),
        contentRating: attributes.contentRating || '',
        availableTranslatedLanguages: attributes.availableTranslatedLanguages || [],
        latestChapter: attributes.latestUploadedChapter ? 'Recently updated' : ''
    };
}

function mapMangaDetail(item, stats = {}) {
    const attributes = item.attributes || {};
    const coverFileName = getCoverFileName(item);
    return {
        mangaId: item.id,
        title: pickLocalizedText(attributes.title) || 'Untitled',
        description: pickLocalizedText(attributes.description) || '',
        coverUrl: buildCoverUrl(item.id, coverFileName),
        status: attributes.status || '',
        year: attributes.year || null,
        tags: mapTags(attributes.tags),
        contentRating: attributes.contentRating || '',
        availableTranslatedLanguages: attributes.availableTranslatedLanguages || [],
        likes: stats.follows || 0,
        views: 0
    };
}

function mapCover(item, fallbackMangaId = '') {
    const attributes = item.attributes || {};
    const mangaRelation = (item.relationships || []).find((relation) => relation.type === 'manga');
    const mangaId = mangaRelation ? mangaRelation.id : fallbackMangaId;
    const fileName = attributes.fileName || '';

    return {
        coverId: item.id,
        mangaId,
        fileName,
        coverUrl: buildCoverUrl(mangaId, fileName),
        thumbnailUrl: buildThumbnailUrl(mangaId, fileName),
        volume: attributes.volume || '',
        locale: attributes.locale || '',
        createdAt: attributes.createdAt || '',
        updatedAt: attributes.updatedAt || ''
    };
}

function mapChapterSummary(item, fallbackMangaId = '') {
    const attributes = item.attributes || {};
    const chapterNumber = attributes.chapter || '';
    const mangaRelation = (item.relationships || []).find((relation) => relation.type === 'manga');
    const title = attributes.title || '';
    return {
        chapterId: item.id,
        mangaId: mangaRelation ? mangaRelation.id : fallbackMangaId,
        title,
        chapterName: title || (chapterNumber ? `Chapter ${chapterNumber}` : 'Chapter'),
        chapter: chapterNumber || null,
        chapterNumber,
        volume: attributes.volume || '',
        translatedLanguage: attributes.translatedLanguage || '',
        language: attributes.translatedLanguage || '',
        publishAt: attributes.publishAt || '',
        readableAt: attributes.readableAt || '',
        createdAt: attributes.createdAt || '',
        updatedAt: attributes.updatedAt || ''
    };
}

function sortChapters(chapters) {
    return (chapters || []).sort((a, b) => {
        const chapterA = parseChapterNumber(a && (a.chapter || a.chapterNumber));
        const chapterB = parseChapterNumber(b && (b.chapter || b.chapterNumber));

        if (chapterA !== chapterB) {
            return chapterA - chapterB;
        }

        return chapterDateValue(a) - chapterDateValue(b);
    });
}

function parseChapterNumber(chapter) {
    if (chapter === null || chapter === undefined) {
        return Number.POSITIVE_INFINITY;
    }

    const value = String(chapter).trim();
    if (!value) {
        return Number.POSITIVE_INFINITY;
    }

    const parsed = Number.parseFloat(value.replace(',', '.'));
    return Number.isFinite(parsed) ? parsed : Number.POSITIVE_INFINITY;
}

function chapterDateValue(chapter) {
    const value = chapter && (chapter.publishAt || chapter.readableAt || chapter.createdAt);
    const time = value ? new Date(value).getTime() : 0;
    return Number.isFinite(time) ? time : 0;
}

function mapGroupSummary(item) {
    const attributes = item.attributes || {};
    return {
        groupId: item.id,
        name: attributes.name || '',
        website: attributes.website || '',
        description: attributes.description || '',
        memberCount: 0,
        mangaCount: 0,
        comicCount: 0,
        followerCount: 0,
        rank: 0
    };
}

function getCoverFileName(item) {
    const cover = (item.relationships || []).find((relation) => relation.type === 'cover_art');
    return cover && cover.attributes && cover.attributes.fileName ? cover.attributes.fileName : '';
}

function buildCoverUrl(mangaId, fileName) {
    if (!mangaId || !fileName) return '';
    return `${MANGADEX_UPLOADS_BASE_URL}/covers/${mangaId}/${fileName}.256.jpg`;
}

function buildThumbnailUrl(mangaId, fileName) {
    return buildCoverUrl(mangaId, fileName);
}

function mapTags(tags) {
    return (tags || [])
        .map((tag) => pickLocalizedText(tag.attributes && tag.attributes.name))
        .filter(Boolean);
}

function pickLocalizedText(values, preferredLanguage = DEFAULT_TRANSLATED_LANGUAGE) {
    if (!values || typeof values !== 'object') return '';
    return values[preferredLanguage]
        || values.en
        || values['en-us']
        || values.vi
        || Object.values(values).find(Boolean)
        || '';
}

function shortenText(value, maxLength) {
    if (!value) return '';
    return value.length > maxLength ? `${value.substring(0, maxLength).trim()}...` : value;
}

function clampNumber(value, min, max, fallback) {
    const numberValue = Number.parseInt(value, 10);
    if (Number.isNaN(numberValue)) return String(fallback);
    return String(Math.max(min, Math.min(max, numberValue)));
}

function normalizeMangaDexError(error, fallbackMessage) {
    if (error.response) {
        const apiError = error.response.data && error.response.data.errors && error.response.data.errors[0];
        const message = (apiError && (apiError.detail || apiError.title)) || fallbackMessage;
        const status = error.response.status;
        const statusCode = status === 404 ? 404 : (status === 429 ? 429 : 502);
        const normalizedError = createHttpError(statusCode, message);

        if (status === 429) {
            const retryAfterSeconds = Number.parseInt(error.response.headers && error.response.headers['retry-after'], 10);
            if (!Number.isNaN(retryAfterSeconds)) {
                normalizedError.retryAfterMs = retryAfterSeconds * 1000;
            }
        }

        return normalizedError;
    }

    if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
        return createHttpError(504, 'MangaDex API request timed out');
    }

    if (error.request || ['ENOTFOUND', 'ECONNRESET', 'ECONNREFUSED', 'EAI_AGAIN'].includes(error.code)) {
        return createHttpError(503, 'Unable to connect to MangaDex API');
    }

    return createHttpError(503, fallbackMessage);
}

function trimTrailingSlash(value) {
    return String(value || '').replace(/\/+$/, '');
}

function createHttpError(statusCode, message) {
    const error = new Error(message);
    error.statusCode = statusCode;
    return error;
}

module.exports = {
    searchManga,
    getMangaDetail,
    getMangaCovers,
    getMangaChapters,
    getChapterPages,
    getGroups,
    searchGroups,
    getGroupDetail,
    getGroupMangaCount
};