const express = require('express');
const groupController = require('../controllers/groupController');

const router = express.Router();

router.get('/', groupController.getGroups);
router.get('/:groupId', groupController.getGroupDetail);

module.exports = router;
