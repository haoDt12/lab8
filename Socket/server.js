const app = require('express')();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const port = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', (socket) => {
    console.log('a user connected');

    socket.emit("new message", "Welcome to the chat!");

    socket.on('join', (username) => {
      // Gửi thông báo có người tham gia cùng tên người dùng mới tham gia
      let joinMessage = username + " joined";
      io.emit("chat message", joinMessage);
  });

   socket.on('chat message', msg => {
        // Khi nhận được tin nhắn từ client, phân tích thông tin người dùng và tin nhắn
        let username = msg.username;
        let message = msg.message;

        // Gửi lại tin nhắn cùng với thông tin người dùng cho tất cả các client
        io.emit('chat message', { username: username, message: message });
        console.log(username + " : " + message);
   });
   
    socket.on('disconnect', () => {
        console.log('user disconnected');
        socket.broadcast.emit("user left", "A user left the chat.");
    });
});

http.listen(port, () => {
  console.log(`Socket.IO server running at http://localhost:${port}/`);
});