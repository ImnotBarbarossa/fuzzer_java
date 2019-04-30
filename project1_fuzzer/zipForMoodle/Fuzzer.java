import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

abstract class Fuzzer {

    /**
     * Function run the converter_static pragram with the file specified by the inputFile variable
     * @param inputFile the input file we give to the exec file.
     * @return true if the execution message contains the crash
     */
    static boolean run_process(Path inputFile) {
        try {
            String line;
            StringBuilder msgExec = new StringBuilder();
            Process p = Runtime.getRuntime().exec("./converter_static " + inputFile + " testoutput.img");
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            /* recover all line of the execution message */
            while ((line = bre.readLine()) != null) {
                msgExec.append(line);
            }
            bre.close();
            p.waitFor();
            return msgExec.toString().toLowerCase().contains("*** the program has crashed.");
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }
    /**
     * Read a file.
     * @param file: the file to read on.
     * @return a byte array from the file.
     */
    private static byte[] read_file(File file) {
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Initializer with the content of the file named "filename"
     * @param filename is the name of file that will be read
     * @return a byte array with the content specific file
     */
    static byte[] initData(String filename) {
        File fileName = new File(filename);
        return read_file(fileName);
    }

    /**
     * Printing the data given in argument for debugging purpose.
     * @param data the tab of byte.
     * @param numberTests the number of tests to do.
     * @param maxNumberModif the maximum number of modif to do.
     * @param percentChange the max percent of random changes.
     */
    private static void print_data(byte[] data, int numberTests, int maxNumberModif, double percentChange) {
        System.out.println("Printing all the data : ");
        System.out.println("NbrTests : " + numberTests);
        System.out.println("MaxNumberOfTests : " + maxNumberModif);
        System.out.println("percentChange : " + percentChange);
        System.out.println("File Data : ");
        for (int i = 0; i < data.length; i++) {
            System.out.println("Data at : " + i + " : ");
            System.out.println(data[i]);
        }
        System.out.println("The data translated : ");
        String str = new String(data, StandardCharsets.UTF_8);
        System.out.println(str);
        System.out.println("----- End -----");
    }
}
