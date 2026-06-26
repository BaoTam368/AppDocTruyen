const express = require('express');
const mangaController = require('../controllers/mangaController');

const router = express.Router();

router.get('/:chapterId/pages', mangaController.getChapterPages);

module.exports = router;
