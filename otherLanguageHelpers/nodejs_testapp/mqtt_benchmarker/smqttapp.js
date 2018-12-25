// content of index.js




const parsed_args = require('yargs').option("server_topic", {
    default:"nodeserver"
}).option("measure", {
    default: 4000
}).argv;


const mqtt = require('mqtt');

const performanceMeter = require("./utils/performance_meter");
const END_CNT = parsed_args.measure;
// const S_TOPIC = parsed_args.server_topic;
// let topic_definition = {};
// topic_definition["S_TOPIC"]
const topic_definition = {
    [parsed_args.server_topic]: 2,
};


var client  = mqtt.connect("tcp://127.0.0.1:1883");

client.on('connect', function () {
    client.subscribe(topic_definition);
    performanceMeter.initialize(END_CNT);
});

client.on('message', function (topic, message, pck) {
    const receivedObject = JSON.parse(message.toString());
    // noinspection JSUnresolvedVariable
    client.publish(receivedObject.respond_to, 'MqResponse from NodeJS', {qos:2});
    performanceMeter.notifyEvent();
});