package d.nrtest.arraysbenchmark;

import d.nrtest.nrcommon.PerformanceMeter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Domen Mori 22. 10. 2018.
 */
public class Benchmark {

    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption("r", "total_runs", true, "How many runs total");
        options.addOption("m", "measure", true, "After how many iterations measure");
        options.addOption("p", "file_path", true, "File to open and test");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int TOTAL_RUNS = Integer.parseInt(cmd.getOptionValue("total_runs", "120000007"));
        // int MEASURE_AT = Integer.parseInt(cmd.getOptionValue("measure", "15000000"));
        int MEASURE_AT = Integer.parseInt(cmd.getOptionValue("measure", "30000000"));
        String FILE_PATH = cmd.getOptionValue("file_path", "C:\\Users\\domen\\Everything\\magisterska\\nodered_resources\\d_resources\\b_ignore\\stringsGen1.2M_32.txt");


        int totalRuns = 0;
        String[] lines = Files.readAllLines(Paths.get(FILE_PATH)).toArray(new String[0]);

        int positionArrayIndex = 0;
        int mainArrayIndex = 0;
        int NUMBER_ARRAYS = Integer.parseInt(lines[0]);
        String[] arrays = Arrays.copyOfRange(lines, 1, NUMBER_ARRAYS + 1);

        int[] positionArray = Arrays.stream(lines[NUMBER_ARRAYS + 1].split(",")).map(Integer::parseInt).mapToInt(Integer::intValue).toArray();  // map(itm => Number(itm));
        boolean[] binaryArray = new boolean[positionArray.length];
        int []idx = new int[]{0};
        Arrays.stream(lines[NUMBER_ARRAYS + 2].split(",")).map(itm -> itm.equals("1")).forEach(item -> binaryArray[idx[0]++] = item);
        int SINGLE_STRING_SIZE = Integer.parseInt(lines[NUMBER_ARRAYS + 3]);
        int MAIN_ARR_SIZE = arrays[0].length();
        int HELPER_ARR_SIZE = positionArray.length;
        PerformanceMeter meter = new PerformanceMeter(MEASURE_AT);
        long BENCHMARK_BEGIN = System.currentTimeMillis();

        String output = "";
        while (totalRuns++ <= TOTAL_RUNS) {
            String payload = arrays[positionArray[positionArrayIndex]].substring(mainArrayIndex, Math.min(mainArrayIndex + SINGLE_STRING_SIZE, MAIN_ARR_SIZE));
            output = binaryArray[positionArrayIndex] ? payload.toUpperCase() : payload.toLowerCase();
            meter.notifyEvent();
            positionArrayIndex = (positionArrayIndex + 1) % HELPER_ARR_SIZE;
            mainArrayIndex = (mainArrayIndex + 1) % MAIN_ARR_SIZE;
        }
        System.out.println("\nDone. Elapsed: " + ((System.currentTimeMillis() - BENCHMARK_BEGIN) * 0.001) + "\nLast string is: " + output);
    }
}

