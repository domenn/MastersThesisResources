package main

import (
	"mqttRunner/pkg/utils"
	//"github.com/TIBCOSoftware/flogo-contrib/action/flow"
	//"github.com/TIBCOSoftware/flogo-contrib/trigger/rest"
	"github.com/TIBCOSoftware/flogo-lib/engine"
	"github.com/TIBCOSoftware/flogo-lib/flogo"
	"github.com/TIBCOSoftware/flogo-lib/logger"
	"io/ioutil"
)

const JSON_PATH = "/home/domen/Everything/magisterska/ALTs/installs/flogo_RES/try_http_update_november_6.json"

func main() {

	app := flogo.NewApp()

	flowJson, err := ioutil.ReadFile(JSON_PATH)
	sharedutils.PanicIfError(err, "Cannot read JSON source file")

	//load flow as json.RawMessage
	app.AddResource("app:myflow", flowJson)

	//trg := app.NewTrigger(&rest.RestTrigger{}, map[string]interface{}{"port": 8080})
	//
	//h1 := trg.NewHandler(map[string]interface{}{"method": "GET", "path": "/blah"})
	//a := h1.NewAction(&flow.FlowAction{}, map[string]interface{}{"flowURI": "res://flow:myflow"})
	//a.SetInputMappings("in1='blah'", "in2=1")
	//a.SetOutputMappings("out1='blah'", "out2=$.flowOut")

	e, err := flogo.NewEngine(app)

	if err != nil {
		logger.Error(err)
		return
	}

	engine.RunEngine(e)
}