const express = require('express');
const postController = require('../controllers/postController');

const router = express.Router();

router.get('/', postController.getPosts);
router.get('/:postId', postController.getPost);
router.post('/', postController.createPost);
router.put('/:postId', postController.updatePost);
router.delete('/:postId', postController.deletePost);
router.post('/:postId/like', postController.toggleLike);

module.exports = router;
