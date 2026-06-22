const express = require('express');
const mangaController = require('../controllers/mangaController');

const router = express.Router();

router.get('/', mangaController.getMangaList);
router.get('/:mangaId/chapters', mangaController.getMangaChapters);
router.get('/:mangaId', mangaController.getMangaDetail);

module.exports = router;
