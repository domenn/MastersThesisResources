package main

import (
	"mqttRunner/pkg/utils/performancemeter"
	"math/rand"
)

const ARRAY_LENGHT = 30000000
var writer int32 = 0

func incrementWriter() int32 {
	r := writer
	writer++
	if writer >= ARRAY_LENGHT{
		writer = 0;
	}
	return r
}

func main() {
	var first [ARRAY_LENGHT]int64
	var second [ARRAY_LENGHT]int64

	for i:=0; i<ARRAY_LENGHT; i++ {
		first[i] = rand.Int63()
		second[i] = rand.Int63()
	}

	meter := performancemeter.Initialize(3699055)

	var results [ARRAY_LENGHT*2]int64

	for {
		results[incrementWriter()] = first[rand.Int31n (ARRAY_LENGHT)] + second[rand.Int31n(ARRAY_LENGHT)];
		results[incrementWriter()] = int64(float64(first[rand.Int31n(ARRAY_LENGHT)]) / float64(second[rand.Int31n(ARRAY_LENGHT)]));
		results[incrementWriter()] = first[rand.Int31n(ARRAY_LENGHT)] % second[rand.Int31n(ARRAY_LENGHT)];
		performancemeter.NotifyEvent(meter)
	}

}