const userService = require('../services/userService');

function createOrUpdateUser(req, res, next) {
    try {
        const data = userService.createOrUpdateUser(req.body);

        res.json({
            success: true,
            data,
            message: 'Đã lưu người dùng'
        });
    } catch (error) {
        next(error);
    }
}

function getUser(req, res, next) {
    try {
        const data = userService.getUser(req.params.userId);

        if (!data) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy người dùng'
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

function updateUser(req, res, next) {
    try {
        const data = userService.updateUser(req.params.userId, req.body);

        res.json({
            success: true,
            data,
            message: 'Đã cập nhật người dùng'
        });
    } catch (error) {
        next(error);
    }
}

module.exports = {
    createOrUpdateUser,
    getUser,
    updateUser
};
