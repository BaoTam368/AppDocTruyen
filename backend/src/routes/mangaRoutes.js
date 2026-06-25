const express = require('express');
const mangaController = require('../controllers/mangaController');

const router = express.Router();

router.get('/', mangaController.getMangaList);
router.get('/chapter/:chapterId/pages', mangaController.getChapterPages);
router.get('/:mangaId/chapters', mangaController.getMangaChapters);
router.get('/:mangaId', mangaController.getMangaDetail);

module.exports = router;
