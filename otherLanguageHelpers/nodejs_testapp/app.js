
var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database(':memory:');

db.serialize(function() {
  db.run("CREATE TABLE datetime_text (d1 TEXT, d2 text)");

  var stmt = db.prepare("INSERT INTO datetime_text VALUES ( datetime('now'), strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime') );");
  stmt.run();
  stmt.finalize();

  db.each("SELECT d1 AS id, d2 as info FROM datetime_text", function(err, row) {
      console.log(row.id + ": " + row.info);
  });
});

db.close();






//const http = require('http');

//const hostname = '127.0.0.1';
//const port = 3000;


/*
const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  console.log('serving request');
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});*/
