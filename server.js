const express = require('express');
const app = express();

app.use(express.json());

const PORT = 3000;

app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(` SERVER NODE.JS BACKEND ĐÃ CHẠY TẠI CỔNG: ${PORT}`);
    console.log(`====================================================`);
});
