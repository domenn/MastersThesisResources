package performancemeter

import (
	"mqttRunner/pkg/utils"
	"fmt"
)

const MS_IN_S = 1000.0

type PerformanceMeter struct {
	startTime           uint64
	startCnt            uint32
	endCnt              uint32
	_1_dividedby_endcnt float64
}

func Initialize(endCnt uint32) *PerformanceMeter {
	item := PerformanceMeter{sharedutils.CurrentTime(), 0, endCnt, 1.0 / float64(endCnt)}
	return &item
}

//Notifies this class that event has happened. Event can be for example, http request. Whatever we are measuring.
//This method prints the performance once endCnt has been reached.
func NotifyEvent(meter *PerformanceMeter) {
	meter.startCnt++
	if meter.startCnt > meter.endCnt {
		meter.startCnt = 0
		msPerRequest := float64(sharedutils.CurrentTime()-meter.startTime) * meter._1_dividedby_endcnt
		fmt.Printf("Events per second: %f\n", (1.0/msPerRequest)*MS_IN_S)
		//fmt.Println("Events per second: ", (1.0 / msPerRequest) * MS_IN_S)
		meter.startTime = sharedutils.CurrentTime()
	}
}
