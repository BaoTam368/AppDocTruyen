const express = require('express');
const chapterController = require('../controllers/chapterController');

const router = express.Router();

router.get('/:chapterId/pages', chapterController.getChapterPages);

module.exports = router;
