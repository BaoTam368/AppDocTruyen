const express = require('express');
const userController = require('../controllers/userController');

const router = express.Router();

router.post('/', userController.createOrUpdateUser);
router.get('/:userId', userController.getUser);
router.put('/:userId', userController.updateUser);

module.exports = router;
