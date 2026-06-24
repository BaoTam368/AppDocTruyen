const mangadexService = require('./mangadexService');
const databaseService = require('./databaseService');

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

async function syncPopularMangas(count = 20) {
    try {
        const mangas = await mangadexService.searchManga({ limit: count });
        
        for (const manga of mangas) {
            try {
                await syncMangaFromMangaDex(manga.mangaId);
                console.log(`Synced: ${manga.title}`);
            } catch (error) {
                console.error(`Failed to sync ${manga.title}:`, error.message);
            }
        }
        
        return mangas;
    } catch (error) {
        console.error('Error syncing popular mangas:', error.message);
        throw error;
    }
}

async function searchAndSync(query) {
    try {
        const mangas = await mangadexService.searchManga({ title: query, limit: 20 });
        
        for (const manga of mangas) {
            try {
                await syncMangaFromMangaDex(manga.mangaId);
            } catch (error) {
                console.error(`Failed to sync ${manga.title}:`, error.message);
            }
        }
        
        return mangas;
    } catch (error) {
        console.error('Error searching and syncing:', error.message);
        throw error;
    }
}

module.exports = {
    syncMangaFromMangaDex,
    syncPopularMangas,
    searchAndSync
};
