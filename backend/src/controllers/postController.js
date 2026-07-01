const postService = require('../services/postService');

function getPosts(req, res, next) {
    try {
        const data = postService.getPosts({
            userId: req.query.userId,
            limit: req.query.limit,
            offset: req.query.offset
        });

        console.log(`[PostController] getPosts: returned ${data ? data.length : 0} posts`);
        res.json({
            success: true,
            data
        });
    } catch (error) {
        console.error('[PostController] getPosts ERROR:', error.message, error.stack);
        next(error);
    }
}

function getPost(req, res, next) {
    try {
        const data = postService.getPostById(req.params.postId);

        if (!data) {
            return res.status(404).json({
                success: false,
                message: 'Post not found'
            });
        }

        res.json({
            success: true,
            data
        });
    } catch (error) {
        console.error('[PostController] getPost ERROR:', error.message, error.stack);
        next(error);
    }
}

function createPost(req, res, next) {
    try {
        const data = postService.createPost(req.body);

        res.status(201).json({
            success: true,
            data,
            message: 'Post created'
        });
    } catch (error) {
        console.error('[PostController] createPost ERROR:', error.message, error.stack);
        next(error);
    }
}

function updatePost(req, res, next) {
    try {
        const data = postService.updatePost(req.params.postId, req.body);

        res.json({
            success: true,
            data,
            message: 'Post updated'
        });
    } catch (error) {
        console.error('[PostController] updatePost ERROR:', error.message, error.stack);
        next(error);
    }
}

function deletePost(req, res, next) {
    try {
        const deleted = postService.deletePost(req.params.postId);

        if (!deleted) {
            return res.status(404).json({
                success: false,
                message: 'Post not found'
            });
        }

        res.json({
            success: true,
            data: null,
            message: 'Post deleted'
        });
    } catch (error) {
        console.error('[PostController] deletePost ERROR:', error.message, error.stack);
        next(error);
    }
}

function toggleLike(req, res, next) {
    try {
        const { postId } = req.params;
        const { userId } = req.body;

        console.log('[PostController] toggleLike request - postId:', postId, 'userId:', userId);

        if (!userId) {
            return res.status(400).json({ success: false, message: 'User is required.' });
        }

        const result = postService.toggleLikePost(postId, userId);
        res.json({
            success: true,
            liked: result.liked,
            likeCount: result.likeCount,
            message: result.liked ? 'Like added' : 'Like removed'
        });
    } catch (error) {
        console.error('[PostController] toggleLike ERROR:', error.message, error.stack);
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            success: false,
            message: error.message || 'Failed to toggle like'
        });
    }
}

module.exports = {
    getPosts,
    getPost,
    createPost,
    updatePost,
    deletePost,
    toggleLike
};
