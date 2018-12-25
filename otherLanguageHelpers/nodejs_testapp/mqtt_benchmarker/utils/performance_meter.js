

let startTime = Date.now();
let startCnt = 0;
let endCnt = 40000;
let _1_dividedby_endcnt = 1.0/endCnt;
const MS_IN_S = 1000.0;

module.exports={
    initialize:function (endcnt) {
        endCnt = endcnt;
        _1_dividedby_endcnt = 1.0/endCnt;
        startTime = Date.now();
    },
    notifyEvent:function () {
        if (startCnt++ > endCnt) {
            startCnt = 0;
            const msPerRequest = (Date.now() - startTime) * _1_dividedby_endcnt;
            console.log("Events per second: " + ((1.0 / msPerRequest) * MS_IN_S));
            startTime = Date.now();
        }
    }
};