const express = require('express');
const localMangaController = require('../controllers/localMangaController');

const router = express.Router();

router.get('/', localMangaController.getLocalMangaList);
router.get('/search', localMangaController.searchLocalMangas);
router.get('/search-sync', localMangaController.searchAndSync);
router.get('/:mangaId', localMangaController.getLocalMangaDetail);
router.get('/:mangaId/chapters', localMangaController.getLocalMangaChapters);
router.post('/:mangaId/sync', localMangaController.syncFromMangaDex);
router.post('/sync/popular', localMangaController.syncPopular);
router.post('/sync/search', localMangaController.searchAndSync);

module.exports = router;
