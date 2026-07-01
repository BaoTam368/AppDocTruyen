const express = require('express');
const groupController = require('../controllers/groupController');

const router = express.Router();

router.get('/', groupController.getGroups);
router.get('/search', groupController.searchGroups);
router.get('/:groupId/manga-count', groupController.getGroupMangaCount);
router.get('/:groupId', groupController.getGroupDetail);

module.exports = router;
