#!/usr/bin/env node

const parsed_args = require('yargs').option("file_path", {
    default:"C:\\Users\\domen\\Everything\\magisterska\\nodered_resources\\d_resources\\b_ignore\\stringsGen1.2M_32.txt"
}).option("measure", {
    default: 30000000
}).option("total_runs", {
    default: 120000007
}).argv;

const fs = require('fs');
const meter = require("./utils/performance_meter");
let totalRuns = 0;

// noinspection JSUnresolvedVariable
fs.readFile(parsed_args.file_path, {encoding: 'ascii'}, function(err,data) {
    if (!err) {
        console.log('Using file: ' + parsed_args.file_path);
        const lines = data.split("\n");
        let positionArrayIndex = 0;
        let mainArrayIndex = 0;
        var arrays = [];
        var NUMBER_ARRAYS = Number(lines[0]);
        for(var i = 0; i<NUMBER_ARRAYS; ++i){
            arrays.push(lines[i+1]);
        }
        var positionArray = lines[NUMBER_ARRAYS+1].split(",").map(itm => Number(itm));
        var binaryArray = lines[NUMBER_ARRAYS+2].split(",").map(itm => itm === "1");
        // !! NOTE: Alteration 1: work with strings, not booleans
        // var binaryArray = lines[NUMBER_ARRAYS+2].split(",");
        var SINGLE_STRING_SIZE = Number(lines[NUMBER_ARRAYS+3]);

        var MAIN_ARR_SIZE = arrays[0].length;
        var HELPER_ARR_SIZE = positionArray.length;

        var runTheSpinner  = function () {
            meter.initialize(parsed_args.measure);
            const BENCHMARK_BEGIN = Date.now();
            // noinspection JSUnresolvedVariable
            let output;
            while(totalRuns++ <= parsed_args.total_runs)
            {
                let payload = arrays[positionArray[positionArrayIndex]].substr(mainArrayIndex, SINGLE_STRING_SIZE);
                output = binaryArray[positionArrayIndex]===true?payload.toUpperCase():payload.toLowerCase();
                meter.notifyEvent();
                positionArrayIndex = (positionArrayIndex+1)%HELPER_ARR_SIZE;
                mainArrayIndex  = (mainArrayIndex+1)%MAIN_ARR_SIZE;
            }
            console.log("\nDone. Elapsed: " , ((Date.now() - BENCHMARK_BEGIN) * 0.001) , "\nLast string is: ", output);
        };

        runTheSpinner();
    }
});