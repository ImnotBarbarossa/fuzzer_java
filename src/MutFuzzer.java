import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class MutFuzzer extends Fuzzer {
    public static void main(String[] args) {

        /* Get the data from the line */
        /* Do this program arg : "testinput.img" 100 50 0.01 */
        File fileName = new File(args[0]);
        int numberTests = Integer.parseInt(args[1]);
        int maxNumberModif = Integer.parseInt(args[2]);
        double percentChange = Double.parseDouble(args[3]);

        /* Read the file */
        byte[] data = read_file(fileName);

        /* Printing all the data for debug purpose */
        // print_data(data, numberTests, maxNumberModif, percentChange);


        /* Run the number of tests asked */
        for (int i = 0; i < numberTests; i++) {
            System.out.println("===== Test " + i + " =====");
            /* Make a copy of the array to randomize on it */
            byte[] dataCopy = new byte[data.length];
            System.arraycopy( data, 0, dataCopy, 0, data.length );

            /* Call the randomize function and test it with the converter exe */
            randomize_data(dataCopy, maxNumberModif, percentChange);
        }
    }

    /**
     * A function to randomize the data array of bytes.
     * @param dataCopy the data array of bytes.
     * @param maxNumberModif the maximum number of modification to do on the array.
     * @param percentChange the maximum percentage of change we can make on the array.
     */
    private static void randomize_data(byte[] dataCopy, int maxNumberModif, double percentChange) {
        int i = 1;
        double currentPercent = 0.0;    // the current percentage of modification.
        /* Copies of the tab */
        byte[] randomArray = new byte[dataCopy.length-1];   // A random array with random bytes.
        boolean[] booleanData = new boolean[dataCopy.length-1]; // A array of boolean to don't modify the same element twice.

        /* while the max number of modification or the max percent is not achieve, we iterate */
        while ((i <= maxNumberModif) && (currentPercent <= percentChange)) {
            /* Random number between 4 and dataCopy.length */
            Random r = new Random();
            int low = 4;    // 4 because we don't want to modify the magic number(ABCD).
            int high = dataCopy.length-1;
            int index = r.nextInt(high-low) + low;

            /* To not modify the same byte 2 or more time, we iterate */
            while (booleanData[index]) {
                index = r.nextInt(high-low) + low;
            }
            /* We fill the random array with random bytes */
            r.nextBytes(randomArray);
            dataCopy[index] = randomArray[index];   // Replacing the element to randomize.
            booleanData[index] = true;  // Set the index of this element to true to don't modify after.

            /*     */

            /* The path where we write the tests files */
            Path inputFile = Paths.get("testInput" + i + ".img");

            /* Write on the file the array of bytes */
            try {
                Files.write(inputFile, dataCopy);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter_static exe */
            String resultOfTheRun = Fuzzer.run_process(inputFile);
            if (resultOfTheRun != null) {   // check if the result is not null
                /* If the program is not crashing we delete the file */
                if (!resultOfTheRun.equals("*** The program has crashed.")) {
                    try {
                        Files.delete(inputFile);
                    } catch (NoSuchFileException x) {
                        System.err.format("%s: no such" + " file or directory%n", inputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // SAVE THE FILE
                    /* Write on the file the array of bytes */
                    Path solutionFile = Paths.get("solutionInput" + i + ".img");
                    try {
                        Files.write(solutionFile, dataCopy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("=============== THE PROGRAM CRASHED ===============");
                    System.out.println("Crash at byte : " + index + " with value : hex : " + String.format("0x%02X", dataCopy[index]) + " OR bytes : " + dataCopy[index]);
                    break;
                }
            }

            /* Calculating the new percent of modif and iterate the max number of modif */
            currentPercent = (double) i / (double) dataCopy.length;
            i++;
        }
    }
}
