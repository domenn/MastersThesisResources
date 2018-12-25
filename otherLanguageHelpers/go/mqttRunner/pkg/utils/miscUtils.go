package sharedutils

import (
	"fmt"
	"log"
	"math"
	"math/rand"
	"os/user"
	"strings"
	"time"
)

func CurrentTime() uint64 {
	return uint64(time.Now().UnixNano() / 1000000)
}

func PanicIfError(e error, errorName string) {
	if e != nil {
		if errorName != "" {
			log.Print("WE GOT ERROR: ", errorName)
		}
		log.Fatal("ERROR: ", e)
		panic(e)
	}
}

func GetIntBetween(min, max int64) int64 {
	return int64(math.Floor(rand.Float64()*float64(max-min) + float64(min)))
	//return int64(math.Floor((rand.Float64() * float64((max-min)) + min));
}

func ArrayToString(a []int32, delim string) string {
	return strings.Trim(strings.Replace(fmt.Sprint(a), " ", delim, -1), "[]")
}

func ArrayToStringInt8(a []int8, delim string) string {
	return strings.Trim(strings.Replace(fmt.Sprint(a), " ", delim, -1), "[]")
}

func GetUserHomeDirectory() string {
	usr, err := user.Current()
	if err != nil {
		log.Fatal(err)
	}
	return usr.HomeDir
}

