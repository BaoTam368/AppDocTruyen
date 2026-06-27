const postService = require('../services/postService');

function getPosts(req, res, next) {
    try {
        const data = postService.getPosts({
            userId: req.query.userId,
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
                message: 'Không tìm thấy bài viết'
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
            message: 'Đã tạo bài viết'
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
            message: 'Đã cập nhật bài viết'
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
                message: 'Không tìm thấy bài viết'
            });
        }

        res.json({
            success: true,
            data: null,
            message: 'Đã xóa bài viết'
        });
    } catch (error) {
        next(error);
    }
}

function toggleLike(req, res, next) {
    try {
        const { postId } = req.params;
        const { userId } = req.body;

        if (!userId) {
            return res.status(400).json({ success: false, message: 'Thiếu userId' });
        }

        const result = postService.toggleLikePost(postId, userId);
        res.json({
            success: true,
            liked: result.liked,
            likeCount: result.likeCount,
            message: result.liked ? 'Đã tăng 1 like' : 'Đã giảm 1 like'
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
    deletePost,
    toggleLike
};
