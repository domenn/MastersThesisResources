#!/usr/bin/env node
// content of index.js

const parsed_args = require('yargs').option("server_topic", {
        default:"nrin"
    }).option("measure", {
        default: 30000000
}).option("client_topic", {
    default: "nodeclient"
}).option("client_name", {
    default: ""
}).option("number_runners", {
    default: 1
}).argv;



const mqtt = require('mqtt');
const performanceMeter = require("./utils/performance_meter");

const END_CNT = parsed_args.measure;
const serverTopic = parsed_args.server_topic;
const client_topic = parsed_args.client_topic;
const client_name = "njsClient: " + parsed_args.client_name;

console.log("Starting client with options:", parsed_args);

var client  = mqtt.connect("tcp://127.0.0.1:1883");

function sendMqttMessage(topicToPublish) {
    client.publish(serverTopic, JSON.stringify({payload:client_name, respond_to:topicToPublish}), {qos:2});
}

function confirmMessageOk(messagePayload){
    if(!messagePayload.startsWith("MqResponse")){
        console.log("HUGE PROBLEM! RETURNED THING IS NOT CORRECT!");
        console.error("HUGE PROBLEM! RETURNED THING IS NOT CORRECT!");
    }
}

client.on('connect', function () {

    performanceMeter.initialize(END_CNT);
   // if (argv.number_runners > 1){
        for (var i = 0; i<parsed_args.number_runners; ++i) {
            const t = client_topic + i;
            client.subscribe(t);
            sendMqttMessage(t);
        }
   // }

});

client.on('message', function (topic, message) {
    const receivedObject = message.toString();
    confirmMessageOk(receivedObject);
    performanceMeter.notifyEvent();
    sendMqttMessage(topic);
});