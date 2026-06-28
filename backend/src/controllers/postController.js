const postService = require('../services/postService');

function getPosts(req, res, next) {
    try {
        const data = postService.getPosts({
            userId: req.query.userId,
            mangaId: req.query.mangaId,
            limit: req.query.limit,
            offset: req.query.offset
        });

        res.json({
            success: true,
            data
        });
    } catch (error) {
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
        next(error);
    }
}

module.exports = {
    getPosts,
    getPost,
    createPost,
    updatePost,
    deletePost
};
