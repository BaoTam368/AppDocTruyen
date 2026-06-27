const commentService = require('../services/commentService');

function getComments(req, res, next) {
    try {
        const data = commentService.getComments({
            mangaId: req.query.mangaId,
            chapterId: req.query.chapterId,
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
            message: 'Đã tạo bình luận'
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
            message: 'Đã cập nhật bình luận'
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
                message: 'Không tìm thấy bình luận'
            });
        }

        res.json({
            success: true,
            data: null,
            message: 'Đã xóa bình luận'
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
