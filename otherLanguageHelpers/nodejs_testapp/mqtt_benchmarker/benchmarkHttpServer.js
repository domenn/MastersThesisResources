// content of index.js

try {
  var dash = require('appmetrics-dash');
  dash.attach();
} catch (ignored) {
  // Ignored
}
// var fs = require('fs');
// const responsePath = ("../../../nodered_resources/d_resources/benchmarkHtmlExample.html");
var respData = Buffer.from(require("./utils/hugeHtmlHardcoded")._html, "utf8");
const http = require('http');
const port = 43220;
const MEASURE_AT = 45000;

const meter = require("./utils/performance_meter");

const requestHandler = (request, response) => {
  // console.log(request.url)

  meter.notifyEvent();

  response.end(respData)
};

const server = http.createServer(requestHandler);
meter.initialize(MEASURE_AT);
server.listen(port, (err) => {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log("server is listening on http://127.0.0.1:" + port)
});

function useless() {
  databaseRows = msg.payload;
  _data = [];
  _dataHumidity = [];

  for (var r = 0; r < databaseRows.length; ++r) {
    // SQLite shrani čas v sekundah, JavaScript pa v milisekundah
    ts = databaseRows[r]["Timestamp"] * 1000;
    tm = (databaseRows[r]["Temperature"]);
    hm = (databaseRows[r]["Humidity"]);

    _data.push({x: ts, y: tm});
    _dataHumidity.push({x: ts, y: hm});
  }
return [{
  payload: [{
    series: ["Temperature"],
    data: [_data],
    labels: [""]
  }]},
  {
    payload: [{
      series: ["Humidity"],
      data: [_dataHumidity],
      labels: [""]
    }]
  }];
}