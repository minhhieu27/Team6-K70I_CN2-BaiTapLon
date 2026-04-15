const express = require('express');
const mysql = require('mysql2');
const app = express();

// 1. Kết nối Database (Bác nhìn kỹ mấy cái chữ host, user, password nhé)
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '123456', 
    database: 'auction_db'
});

db.connect(err => {
    if (err) {
        return console.log('Lỗi kết nối: ' + err.message);
    }
    console.log('MySQL đã thông! Đêm nay cày nốt là xong BTL bác ơi.');
});
// Cho phép server mở file index.html
app.get('/', (req, res) => {
    res.sendFile(__dirname + '/index.html');
});
// 2. API lấy hàng từ kho ra web
app.get('/products', (req, res) => {
    db.query('SELECT * FROM products', (err, results) => {
        if (err) return res.send('Lỗi truy vấn rồi bác');
        res.json(results); 
    });
});

app.listen(3000, () => {
    console.log('Web đang chạy tại: http://localhost:3000');
});