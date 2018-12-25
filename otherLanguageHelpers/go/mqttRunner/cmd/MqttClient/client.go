package main

import (
	"fmt"
	"github.com/eclipse/paho.mqtt.golang"
	"math/rand"
)
const SERVER_TOPIC = "goflomqs"
var f mqtt.MessageHandler = func(client mqtt.Client, msg mqtt.Message) {
	fmt.Printf("TOPIC: %s\n", msg.Topic())
	fmt.Printf("MSG: %s\n", msg.Payload())
}

var received mqtt.MessageHandler = func(client mqtt.Client, msg mqtt.Message) {
	//fmt.Printf("TOPIC: %s\n", msg.Topic())
	//fmt.Printf("MSG: %s\n", msg.Payload())
	doCommand(client)
}

func main() {


	opts := mqtt.NewClientOptions().AddBroker("tcp://127.0.0.1:1883").SetClientID("GOED" + string(rand.Intn(100000)))
	opts.SetDefaultPublishHandler(f)
	c := mqtt.NewClient(opts)
	if token := c.Connect(); token.Wait() && token.Error() != nil {
		panic(token.Error())
	}

	subToken := c.Subscribe("itsgone", 2, received)
	//if token := ; token.Wait() && token.Error() != nil {
	//	fmt.Println(token.Error())
	//	os.Exit(1)
	//}

	doCommand(c)

	subToken.Wait()

	fmt.Scanln()

	//mqtt.DEBUG = log.New(os.Stdout, "", 0)
	//mqtt.ERROR = log.New(os.Stdout, "", 0)
	//opts := mqtt.NewClientOptions().AddBroker("tcp://iot.eclipse.org:1883").SetClientID("gotrivial")
	//opts.SetKeepAlive(2 * time.Second)
	//opts.SetDefaultPublishHandler(f)
	//opts.SetPingTimeout(1 * time.Second)
	//
	//c := mqtt.NewClient(opts)
	//if token := c.Connect(); token.Wait() && token.Error() != nil {
	//	panic(token.Error())
	//}
	//
	//if token := c.Subscribe("go-mqtt/sample", 0, nil); token.Wait() && token.Error() != nil {
	//	fmt.Println(token.Error())
	//	os.Exit(1)goflomqs
	//}
	//
	//for i := 0; i < 5; i++ {
	//	text := fmt.Sprintf("this is msg #%d!", i)
	//	token := c.Publish("go-mqtt/sample", 0, false, text)
	//	token.Wait()
	//}
	//
	//time.Sleep(6 * time.Second)
	//
	//if token := c.Unsubscribe("go-mqtt/sample"); token.Wait() && token.Error() != nil {
	//	fmt.Println(token.Error())
	//	os.Exit(1)
	//}
	//
	//c.Disconnect(250)
	//
	//time.Sleep(1 * time.Second)
}
func doCommand(client mqtt.Client) {
	_ = client.Publish(SERVER_TOPIC, 2, false, "{\"payload\":\"Finalizing GO\",\"respond_to\":\"itsgone\"}")
	//token.Wait()
}