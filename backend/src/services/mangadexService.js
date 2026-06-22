const axios = require('axios');

const MANGADEX_BASE_URL = process.env.MANGADEX_BASE_URL || 'https://api.mangadex.org';
const configuredTimeout = Number.parseInt(process.env.REQUEST_TIMEOUT_MS || '10000', 10);
const REQUEST_TIMEOUT_MS = Number.isNaN(configuredTimeout) ? 10000 : configuredTimeout;

const mangadexClient = axios.create({
    baseURL: MANGADEX_BASE_URL,
    timeout: REQUEST_TIMEOUT_MS,
    headers: {
        'User-Agent': 'AppDocTruyenStudentDemo/1.0'
    }
});

const DEMO_GROUPS = [
    {
        groupId: 'demo-group-1',
        name: 'Ánh Dương Team',
        description: 'Nhóm dịch truyện phiêu lưu và hành động.',
        comicCount: 18,
        memberCount: 860,
        followerCount: 1450
    },
    {
        groupId: 'demo-group-2',
        name: 'Hikari Scan',
        description: 'Nhóm dịch truyện học đường và đời thường.',
        comicCount: 24,
        memberCount: 1240,
        followerCount: 2310
    },
    {
        groupId: 'demo-group-3',
        name: 'Manga Việt Group',
        description: 'Nhóm cộng tác dịch nhiều thể loại truyện mới.',
        comicCount: 31,
        memberCount: 1750,
        followerCount: 2680
    }
];

async function searchManga({title = '', limit = 20, offset = 0}) {
    const params = new URLSearchParams();
    params.set('limit', clampNumber(limit, 1, 50, 20));
    params.set('offset', clampNumber(offset, 0, 10000, 0));
    params.append('includes[]', 'cover_art');
    params.append('contentRating[]', 'safe');
    params.append('contentRating[]', 'suggestive');
    params.set('order[latestUploadedChapter]', 'desc');

    if (title && title.trim()) {
        params.set('title', title.trim());
    }

    try {
        const response = await mangadexClient.get('/manga', {params});
        return (response.data.data || []).map(mapMangaSummary);
    } catch (error) {
        throw normalizeMangaDexError(error, 'Không thể lấy danh sách truyện từ MangaDex');
    }
}

async function getMangaDetail(mangaId) {
    if (!mangaId || !mangaId.trim()) {
        throw createHttpError(400, 'Thiếu mangaId');
    }

    const params = new URLSearchParams();
    params.append('includes[]', 'cover_art');

    try {
        const response = await mangadexClient.get(`/manga/${mangaId}`, {params});
        if (!response.data.data) {
            throw createHttpError(404, 'Không tìm thấy truyện');
        }
        return mapMangaDetail(response.data.data);
    } catch (error) {
        if (error.statusCode) throw error;
        throw normalizeMangaDexError(error, 'Không thể lấy chi tiết truyện từ MangaDex');
    }
}

async function getMangaChapters(mangaId, {limit = 50, offset = 0, language = 'en'} = {}) {
    if (!mangaId || !mangaId.trim()) {
        throw createHttpError(400, 'Thiếu mangaId');
    }

    const params = new URLSearchParams();
    params.set('limit', clampNumber(limit, 1, 100, 50));
    params.set('offset', clampNumber(offset, 0, 10000, 0));
    params.append('translatedLanguage[]', language || 'en');
    params.set('order[chapter]', 'asc');

    try {
        const response = await mangadexClient.get(`/manga/${mangaId}/feed`, {params});
        return (response.data.data || []).map(mapChapterSummary);
    } catch (error) {
        throw normalizeMangaDexError(error, 'Không thể lấy danh sách chapter từ MangaDex');
    }
}

async function getChapterPages(chapterId) {
    if (!chapterId || !chapterId.trim()) {
        throw createHttpError(400, 'Thiếu chapterId');
    }

    try {
        const response = await mangadexClient.get(`/at-home/server/${chapterId}`);
        const chapter = response.data.chapter;
        if (!chapter || !chapter.hash || !Array.isArray(chapter.data)) {
            throw createHttpError(404, 'Không tìm thấy ảnh của chapter');
        }

        return {
            chapterId,
            pages: chapter.data.map((fileName) =>
                `${response.data.baseUrl}/data/${chapter.hash}/${fileName}`
            )
        };
    } catch (error) {
        if (error.statusCode) throw error;
        throw normalizeMangaDexError(error, 'Không thể lấy ảnh đọc truyện từ MangaDex');
    }
}

async function getGroups({limit = 20, offset = 0} = {}) {
    const params = new URLSearchParams();
    params.set('limit', clampNumber(limit, 1, 50, 20));
    params.set('offset', clampNumber(offset, 0, 10000, 0));

    try {
        const response = await mangadexClient.get('/group', {params});
        const groups = (response.data.data || []).map(mapGroupSummary);
        return groups.length > 0 ? groups : DEMO_GROUPS;
    } catch (error) {
        return DEMO_GROUPS;
    }
}

async function getGroupDetail(groupId) {
    if (!groupId || !groupId.trim()) {
        throw createHttpError(400, 'Thiếu groupId');
    }

    try {
        const response = await mangadexClient.get(`/group/${groupId}`);
        if (!response.data.data) {
            throw createHttpError(404, 'Không tìm thấy nhóm dịch');
        }
        return mapGroupSummary(response.data.data);
    } catch (error) {
        const fallback = DEMO_GROUPS.find((group) => group.groupId === groupId || String(group.id) === groupId);
        if (fallback) return fallback;
        if (error.statusCode) throw error;
        return {
            groupId,
            name: 'Nhóm dịch demo',
            description: 'Dữ liệu nhóm dịch demo khi MangaDex group API không sẵn sàng.',
            comicCount: 0,
            memberCount: 0,
            followerCount: 0
        };
    }
}

function mapMangaSummary(item) {
    const attributes = item.attributes || {};
    return {
        mangaId: item.id,
        title: pickLocalizedText(attributes.title) || 'Chưa có tên',
        description: shortenText(pickLocalizedText(attributes.description), 180),
        coverUrl: buildCoverUrl(item),
        latestChapter: attributes.latestUploadedChapter ? 'Mới cập nhật' : ''
    };
}

function mapMangaDetail(item) {
    const attributes = item.attributes || {};
    return {
        mangaId: item.id,
        title: pickLocalizedText(attributes.title) || 'Chưa có tên',
        description: pickLocalizedText(attributes.description) || '',
        coverUrl: buildCoverUrl(item),
        status: attributes.status || '',
        year: attributes.year || null,
        tags: (attributes.tags || []).map((tag) => pickLocalizedText(tag.attributes && tag.attributes.name)).filter(Boolean)
    };
}

function mapChapterSummary(item) {
    const attributes = item.attributes || {};
    const chapterNumber = attributes.chapter || '';
    return {
        chapterId: item.id,
        chapterName: attributes.title || (chapterNumber ? `Chapter ${chapterNumber}` : 'Chapter'),
        chapterNumber,
        language: attributes.translatedLanguage || '',
        createdAt: attributes.createdAt || ''
    };
}

function mapGroupSummary(item, index = 0) {
    const attributes = item.attributes || {};
    return {
        groupId: item.id,
        name: attributes.name || 'Nhóm dịch',
        description: attributes.description || 'Nhóm dịch trên MangaDex',
        comicCount: 10 + index * 2,
        memberCount: 120 + index * 45,
        followerCount: 300 + index * 80,
        rank: index + 1
    };
}

function buildCoverUrl(item) {
    const cover = (item.relationships || []).find((relation) => relation.type === 'cover_art');
    const fileName = cover && cover.attributes && cover.attributes.fileName;
    if (!fileName) return '';
    return `https://uploads.mangadex.org/covers/${item.id}/${fileName}.256.jpg`;
}

function pickLocalizedText(values) {
    if (!values || typeof values !== 'object') return '';
    return values.vi || values.en || values['en-us'] || Object.values(values).find(Boolean) || '';
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
        const statusCode = error.response.status === 404 ? 404 : 502;
        return createHttpError(statusCode, message);
    }

    if (error.code === 'ECONNABORTED') {
        return createHttpError(504, 'MangaDex API timeout');
    }

    return createHttpError(503, fallbackMessage);
}

function createHttpError(statusCode, message) {
    const error = new Error(message);
    error.statusCode = statusCode;
    return error;
}

module.exports = {
    searchManga,
    getMangaDetail,
    getMangaChapters,
    getChapterPages,
    getGroups,
    getGroupDetail
};
