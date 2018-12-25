package main

import (
	"mqttRunner/pkg/utils/performancemeter"
	"fmt"
	"log"
	"mqttRunner/cmd/HttpServer/resources"

	"net/http"
)

var meter *performancemeter.PerformanceMeter
const SERVER_PERFORMANCE_CNT = 30000

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, resources.HTML)
	performancemeter.NotifyEvent(meter)
}

func main() {
	http.HandleFunc("/goserv", handler)
	meter = performancemeter.Initialize(SERVER_PERFORMANCE_CNT)
	log.Fatal(http.ListenAndServe(":31011", nil))
}

/*
 per second: 2713704.2062415197
Events per second: 2728512.9604365625
Events per second: 2710027.100271003
Events per second: 2699055.3306342782
Events per second: 2574002.574002574
Events per second: 2503128.911138924
Events per second: 2538071.0659898478
Events per second: 2262443.4389140275
Events per second: 2512562.814070352
 */