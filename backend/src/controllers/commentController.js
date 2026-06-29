const commentService = require('../services/commentService');

function getComments(req, res, next) {
    try {
        const data = commentService.getComments({
            mangaId: req.query.mangaId || req.query.manga_id,
            chapterId: req.query.chapterId || req.query.chapter_id,
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

function createComment(req, res, next) {
    try {
        const data = commentService.createComment(req.body);

        res.status(201).json({
            success: true,
            data,
            message: 'Comment created'
        });
    } catch (error) {
        next(error);
    }
}

function updateComment(req, res, next) {
    try {
        const data = commentService.updateComment(req.params.commentId, req.body);

        res.json({
            success: true,
            data,
            message: 'Comment updated'
        });
    } catch (error) {
        next(error);
    }
}

function deleteComment(req, res, next) {
    try {
        const deleted = commentService.deleteComment(req.params.commentId);

        if (!deleted) {
            return res.status(404).json({
                success: false,
                message: 'Comment not found'
            });
        }

        res.json({
            success: true,
            data: null,
            message: 'Comment deleted'
        });
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getComments,
    createComment,
    updateComment,
    deleteComment
};