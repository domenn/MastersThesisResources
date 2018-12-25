package main

import (
	"fmt"
	"math"
	"mqttRunner/cmd/HelperExecutables/items"
	"mqttRunner/pkg/utils"
	"os"
)

const PATH_TO_GEN = `C:\Users\domen\Everything\magisterska\nodered_resources\d_resources\b_ignore\stringsGen700k_14.txt`

//const ARRAY_LENGTH = 12000000
//const NUMBER_ARRAYS = 36
const ARRAY_LENGTH = 800
const NUMBER_ARRAYS = 4
const GENERATE_MINIMUM = 32
const GENERATE_MAXIMUM = 125
const SINGLE_STRING_SIZE = 14;

func appendFile(fn, text string) {
	f, err := os.OpenFile(fn, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	sharedutils.PanicIfError(err, "Appending file failed -- open file")
	_, err = f.WriteString(text)
	sharedutils.PanicIfError(err, "Appending file failed -- write")
	f.Close()
}

func appendFileByte(fn string, text []uint8) {
	f, err := os.OpenFile(fn, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	sharedutils.PanicIfError(err, "appendFileByte failed -- open file")
	_, err = f.Write(text)
	sharedutils.PanicIfError(err, "appendFileByte failed -- write")
	f.Close()
}

func main() {

	argsWithProg := os.Args
	if len(argsWithProg) > 1 && argsWithProg[1] == "generator" {
		println("Running generator");
	} else {
		items.ArraysBenchmark()
		return
	}

	DIAGNOSTIC_TIMESTAMP_BEGIN := sharedutils.CurrentTime();
	f, err := os.OpenFile(PATH_TO_GEN, os.O_TRUNC|os.O_CREATE, 0644)
	sharedutils.PanicIfError(err, "Appending file failed -- open file")
	f.Close()

	HELPER_ARR_SIZE := int32(math.Ceil(float64(ARRAY_LENGTH) / 71.0));
	helper1 := make([]int32, HELPER_ARR_SIZE)
	binary := make([]int8, HELPER_ARR_SIZE)
	for i := int32(0); i < HELPER_ARR_SIZE; i++ {
		helper1[i] = int32(sharedutils.GetIntBetween(0, NUMBER_ARRAYS));
		binary[i] = int8(sharedutils.GetIntBetween(0, 2));
	}
	helper1Str := sharedutils.ArrayToString(helper1, ",")
	binaryStr := sharedutils.ArrayToStringInt8(binary, ",")

	var arrayBuffer [ARRAY_LENGTH] uint8
	newline := [...]uint8{'\n'}
	appendFile(PATH_TO_GEN, fmt.Sprintf("%d", NUMBER_ARRAYS)+"\n")
	for i := 0; i < NUMBER_ARRAYS; i++ {
		for i := 0; i < ARRAY_LENGTH; i++ {
			arrayBuffer[i] = uint8(sharedutils.GetIntBetween(GENERATE_MINIMUM, GENERATE_MAXIMUM));
		}
		appendFileByte(PATH_TO_GEN, arrayBuffer[:])
		appendFileByte(PATH_TO_GEN, newline[:])
		// b := a[:] // Same as b := a[0:len(a)]
	}

	appendFile(PATH_TO_GEN, helper1Str+"\n")
	appendFile(PATH_TO_GEN, binaryStr+"\n")
	appendFile(PATH_TO_GEN, fmt.Sprintf("%d", SINGLE_STRING_SIZE))
	DIAGNOSTIC_TIMESTAMP_LATEST := sharedutils.CurrentTime();
	fmt.Println("Time taken: ", float32(DIAGNOSTIC_TIMESTAMP_LATEST-DIAGNOSTIC_TIMESTAMP_BEGIN)*0.001)
}
