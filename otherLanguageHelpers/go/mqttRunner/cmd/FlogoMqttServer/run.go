package main

import (
	"mqttRunner/pkg/utils/performancemeter"
	"context"
	"encoding/json"
	"github.com/TIBCOSoftware/flogo-contrib/trigger/mqtt"
	"github.com/TIBCOSoftware/flogo-lib/core/data"
	"github.com/TIBCOSoftware/flogo-lib/engine"
	"github.com/TIBCOSoftware/flogo-lib/flogo"
	"github.com/TIBCOSoftware/flogo-lib/logger"
	mqttresp "github.com/jvanderl/flogo-components/activity/mqtt"
)
// mqttresp "github.com/jvanderl/flogo-components/activity/mqtt"

type msg struct {
	Payload    string
	Respond_to string
}

const TOOL_SERVER_TOPIC = "goflomqs"
const TOOL_PERFORMANCE_CNT = 2000

var meter *performancemeter.PerformanceMeter


//go:generate go run $GOPATH/src/github.com/TIBCOSoftware/flogo-lib/flogo/gen/gen.go $GOPATH

func main() {

	// Create our Flogo app
	app := flogo.NewApp()

	// Setup our event trigger (HTTP REST in this case).

	// Listen on port 9999
	// trg := app.NewTrigger(&rest.RestTrigger{}, map[string]interface{}{"port": 9999})
	trg := app.NewTrigger(&mqtt.MqttTrigger{}, map[string]interface{}{"broker": "tcp://localhost:1883", "id":"goflogoq", "qos":2 })

	// Create a Function Handler for verb: GET and URI path: /blah
	trg.NewFuncHandler(map[string]interface{}{"topic": TOOL_SERVER_TOPIC}, HandlerMqttServer)




	// Create the Flogo engine
	e, err := flogo.NewEngine(app)
	meter = performancemeter.Initialize(TOOL_PERFORMANCE_CNT)
	if err != nil {
		logger.Error(err)
		return
	}

	// Start your engine!
	engine.RunEngine(e)
}

func HandlerMqttServer(ctx context.Context, inputs map[string]*data.Attribute) (map[string]*data.Attribute, error) {
	received := inputs["message"].Value().(string)
	// logger.Infof("%s", received)
	var msgobject msg
	err := json.Unmarshal([]byte(received), &msgobject)
	if err != nil {
		logger.Infof("Received message is not JSON convertable, something is wrong.\nMes: %s", received)
		return nil, nil
	}
	answerMqtt(msgobject.Respond_to)

	return nil, nil
}
func answerMqtt(topic string) {
	response := make(map[string]interface{})
	response["qos"]=2
	response["broker"]="tcp://localhost:1883"
	response["message"]="MqResponse: GO thingy super complicated"
	response["topic"]=topic

	performancemeter.NotifyEvent(meter)

	// mqttMetadata := activity.NewMetadata(resource.MqttJson)

	// flogo.EvalActivity(&mqttresp.MyActivity{mqttMetadata}, response)
	flogo.EvalActivity(mqttresp.NewActivity(nil), response)
	// flogo.EvalActivity(&log.LogActivity{}, in)
//	act := mqttresp.NewActivity(mqttMetadata)

//	act.Eval(response)
}

// HandleHttpEvent handles events being dispatched by the function handler.
// All GET requests to http://localhost:9999/blah events will handled by this function
//func HandleHttpEvent(ctx context.Context, inputs map[string]*data.Attribute) (map[string]*data.Attribute, error) {
//
//	logger.Infof("#v", inputs)
//
//	return nil, nil
//}