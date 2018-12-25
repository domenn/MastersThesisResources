package main

import (
	"mqttRunner/pkg/utils/performancemeter"
	"encoding/json"
	"fmt"
	"github.com/eclipse/paho.mqtt.golang"
	"math/rand"
)

const SERVER_TOPIC = "goserver"
const SERVER_PERFORMANCE_CNT = 30000

var meter *performancemeter.PerformanceMeter

type msg struct {
	Payload    string
	Respond_to string
}

var received mqtt.MessageHandler = func(client mqtt.Client, message mqtt.Message) {
	var msgobject msg
	json.Unmarshal([]byte(message.Payload()), &msgobject)
	performancemeter.NotifyEvent(meter)
	_ = client.Publish(msgobject.Respond_to, 2, false, "MqResponse from Go server")
}



func main() {
	opts := mqtt.NewClientOptions().AddBroker("tcp://127.0.0.1:1883").SetClientID("GOED" + string(rand.Intn(100000)))
	c := mqtt.NewClient(opts)
	if token := c.Connect(); token.Wait() && token.Error() != nil {
		panic(token.Error())
	}
	meter = performancemeter.Initialize(SERVER_PERFORMANCE_CNT)
	subToken := c.Subscribe(SERVER_TOPIC, 2, received)

	subToken.Wait()

	fmt.Scanln()
}
