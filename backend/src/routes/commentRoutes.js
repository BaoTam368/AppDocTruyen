const express = require('express');
const commentController = require('../controllers/commentController');

const router = express.Router();

router.get('/', commentController.getComments);
router.post('/', commentController.createComment);
router.put('/:commentId', commentController.updateComment);
router.delete('/:commentId', commentController.deleteComment);

module.exports = router;
