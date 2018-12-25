


const ARRAY_LENGHT = 30000000;
const ARR_MAX_VALKUE = Math.pow(2,63)-1;
let writer = 0;

function incrementWriter(){
    var r = writer++;
    if (writer >= ARRAY_LENGHT){
        writer = 0;
    }
    return r;
}


var meter = require("./utils/performance_meter");
meter.initialize(1000000);



var first = [];
var second = [];

for(var i = 0; i<ARRAY_LENGHT; ++i){
    first.push(Math.random() * ARR_MAX_VALKUE);
    second.push(Math.random() * ARR_MAX_VALKUE);
}

var results = [];
results[ARRAY_LENGHT-1] = 0;

while (true){
    results[incrementWriter()] = first[Math.random() * ARRAY_LENGHT - 1.0] + second[Math.random() * ARRAY_LENGHT - 1.0];
    results[incrementWriter()] = parseInt(first[Math.random() * ARRAY_LENGHT - 1.0] / [Math.random() * ARRAY_LENGHT - 1.0], 10);
    results[incrementWriter()] = first[Math.random() * ARRAY_LENGHT - 1.0] % second[Math.random() * ARRAY_LENGHT - 1.0];
    meter.notifyEvent();
}
