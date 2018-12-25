package items

import (
	"mqttRunner/pkg/utils"
	"mqttRunner/pkg/utils/performancemeter"
	"fmt"
	"io/ioutil"
	"path"
	"strconv"
	"strings"
)

const MEASURE_AT = 30000000
const TOTAL_RUNS = 120000007
//const FILE_PATH = "C:\\Users\\domen\\Everything\\magisterska\\nodered_resources\\d_resources\\b_ignore\\stringsGen1.2M_32.txt"
// const FILE_PATH = `../../Everything/magisterska/nodered_resources/d_resources/b_ignore/stringsGen1.2M_32.txt`

func minInt(x, y int) int {
	if x < y {
		return x
	}
	return y
}

func ArraysBenchmark() {

	filePath := path.Join(sharedutils.GetUserHomeDirectory(), "Everything", "magisterska", "nodered_resources", "d_resources", "b_ignore", "stringsGen1.2M_32.txt")


	totalRuns := 0
	fileContent, err := ioutil.ReadFile(filePath)
	sharedutils.PanicIfError(err, "Reading strings file")

	fileContentStr := string(fileContent)
	lines := strings.Split(fileContentStr, "\n")
	tmpNumberArrays, err := strconv.ParseInt(lines[0], 0, 32)
	NUMBER_ARRAYS := int(tmpNumberArrays)
	MAIN_ARR_SIZE := len(lines[1])
	positionArrayIndex := 0
	mainArrayIndex := 0
	// arrays := make([]string, NUMBER_ARRAYS*MAIN_ARR_SIZE)
	// board[i*NUMBER_ARRAYS + j] = "abc" // like board[i][j] = "abc"

	contentStartsAt := len(lines[0]) + 1 // 1 for \n
	arrays := make([]string, NUMBER_ARRAYS)
	for i := 0; i < NUMBER_ARRAYS; i++ {
		// Additional +i for \n at end
		arr := fileContentStr[i*MAIN_ARR_SIZE+contentStartsAt+i : i*MAIN_ARR_SIZE+contentStartsAt+MAIN_ARR_SIZE+i]
		arrays[i] = arr
	}

	tempStringNumbersArray := strings.Split(lines[NUMBER_ARRAYS+1], ",")
	var HELPER_ARR_SIZE = len(tempStringNumbersArray);
	positionArray := make([]int, HELPER_ARR_SIZE)
	for index, item := range tempStringNumbersArray {
		j, err := strconv.Atoi(item)
		sharedutils.PanicIfError(err, "Parse int from helper array")
		positionArray[index] = j
	}
	tempStringNumbersArray = strings.Split(lines[NUMBER_ARRAYS+2], ",")
	binaryArray := make([]int8, HELPER_ARR_SIZE)
	for index, item := range tempStringNumbersArray {
		j, err := strconv.ParseInt(item, 0, 8)
		sharedutils.PanicIfError(err, "Parse int from helper array")
		binaryArray[index] = int8(j)
	}
	var parseReturn, _ = strconv.ParseInt(lines[NUMBER_ARRAYS+3], 0, 32)
	SINGLE_STRING_SIZE := int(parseReturn)
	//SINGLE_STRING_SIZE := 30 + int(parseReturn)

	meter := performancemeter.Initialize(MEASURE_AT)
	BENCHMARK_BEGIN := sharedutils.CurrentTime()
	// noinspection JSUnresolvedVariable
	var output string
	for totalRuns <= TOTAL_RUNS {
		totalRuns++
		// TEST, if needed do math.min with len()
		payload := arrays[positionArray[positionArrayIndex]][mainArrayIndex : minInt(mainArrayIndex+SINGLE_STRING_SIZE, MAIN_ARR_SIZE)]
		lowerOrUpper := binaryArray[positionArrayIndex] == 1
		if lowerOrUpper {
			output = strings.ToUpper(payload)
		} else {
			output = strings.ToLower(payload)
		}
		performancemeter.NotifyEvent(meter)
		positionArrayIndex = (positionArrayIndex + 1) % HELPER_ARR_SIZE;
		mainArrayIndex = (mainArrayIndex + 1) % MAIN_ARR_SIZE;
	}
	fmt.Print("\nDone. Elapsed: ", (float64(sharedutils.CurrentTime() - BENCHMARK_BEGIN) * 0.001), "\nLast string is: ", output);
};