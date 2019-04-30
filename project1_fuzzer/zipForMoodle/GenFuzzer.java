import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenFuzzer extends Fuzzer{

    public static void main(String [] args){
        /* Initialise data to the good format */
        byte[] data =initData("testinput.img");

        /* Crash test about negative value for the width or the height */
        System.out.println("===== Test negative picture size =====");
        negativeDimensionPicture(data, Paths.get("fileCrashFromGenFuzzer/testInputGen1.img"));

        /* Crash test about the number color is upper than 256 */
        System.out.println("===== Test upper 256 value for the number color =====");
        testOnNumberColor(data, Paths.get("fileCrashFromGenFuzzer/testInputGen2.img"));

        /* Crash test  about author name is to big */
        System.out.println("===== Test big author name =====");
        testOnTheAuthorName(data,Paths.get("fileCrashFromGenFuzzer/testInputGen3.img"));

        /* Crash test about width and height too large */
        System.out.println("===== Test huge picture size =====");
        testOnTheHugeDimension(data,Paths.get("fileCrashFromGenFuzzer/testInputGen4.img"));

        /* Crash test about old version */
        System.out.println("===== Test old version =====");
        testOnTheOldVersion(data,Paths.get("fileCrashFromGenFuzzer/testInputGen5.img"));

        /* Crash test about a little table of colors */
        System.out.println("===== Test a little table of color =====");
        testLittleColorTable(data,Paths.get("fileCrashFromGenFuzzer/testInputGen6.img"));

        /* Crash test about a little table of pixels */
        System.out.println("===== Test a little table of pixels =====");
        testLittlePixelTable(data, Paths.get("fileCrashFromGenFuzzer/testInputGen7.img"));
    }

    /**
     *  Crash test about the size of the pixels table
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testLittlePixelTable(byte[] data, Path path) {
        byte [] crashData = genDataWithLittlePixelsTable(data);
        try {
            Files.write(path,crashData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Run the converter_static exe */
        if (testOnConverter(run_process(path),path)){
            System.out.println("[FOUND]: Crash about a little table of pixels");
        }
    }

    /**
     *  Crash test about the size of the colors table
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testLittleColorTable(byte[] data, Path path) {
        byte[] crashData = genDataWithLittleColorsTable(data);
        try {
            Files.write(path,crashData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Run the converter_static exe */
        if (testOnConverter(run_process(path),path)){
            System.out.println("[FOUND]: Crash about a little table of colors");
        }
    }

    /**
     *  Crash test about old version of the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheOldVersion(byte[] data, Path path) {
        byte [] crashData;
        for (int i = 0; i < 100; i++) {
            crashData= genDataWithSpecificVersion(data, (byte) i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about an old version : v-"+i);
                return;
            }
        }
    }

    /**
     *  Crash test about dimension of the pixels table can have
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheHugeDimension(byte[] data, Path path) {
        byte [] crashData;
        int [] hexaIndex = new int[]{11,15};
        for (int i = 200; i <256; i++) {
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte) i, (byte) i});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about huge picture dimension");
            }
        }
    }

    /**
     *  Crash test about the size of the author name in the input file for the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheAuthorName(byte[] data, Path path) {
        byte [] crashData;
        for (int i = 20; i < 1000; i+=10) {
            crashData= genDataWithBigName(data,i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND]: Crash about the author name with length: " + i);
                return;
            }
        }
    }

    /**
     *  Crash test about number of colors the color table can contain
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnNumberColor(byte[] data, Path path) {
        byte[] crashData;
        for (int i = 0; i < 256; i++) {// 256 is the max value for a byte
            crashData=genCrashData(data,21,(byte)i); // 21 it's the byte position to make a big number of color
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about the color number upper than 256");
                return;
            }
        }
    }

    /**
     * Generator of a small pixels table
     * @param data is a byte array with the good format for the input converter_static program
     * @return a byte array based on the data variable with pixels table with one pixel
     */
    private static byte[] genDataWithLittlePixelsTable(byte[] data) {
        byte [] newData = new byte[data.length-(16*16)+1];// 16*16 is the size of the old table's pixels and 1 for the new table
        System.arraycopy(data,0,newData,0,data.length-(16*16));
        newData[newData.length-1]=(byte)Math.floor(Math.random()*256);
        return newData;
    }

    /**
     * Generator of a small colors table
     * @param data is a byte array with the good format for the input converter_static program
     * @return a byte array based on the data variable with colors table with one color
     */
    private static byte[] genDataWithLittleColorsTable(byte[] data) {
        byte [] newData = new byte[data.length-(4*4)+4]; // 4*4 is the size of the old table's colors and last 4 is for the new
        System.arraycopy(data,0,newData,0,21);//copy all data before the color table

        /* generate one color for the table */
        for (int i = 22; i < 27; i++) { //22 and 27 is the zone in data where the color table is
            newData[i]= (byte) (Math.floor(Math.random()*256));
        }
        /* copy the rest of the data in the newData */
        for (int i = 27,j=38; i < newData.length && j < data.length; i++,j++) {
            newData[i]=data[j];
        }
        return newData;
    }

    /**
     *  Crash test about the value of height particularly negative value
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void negativeDimensionPicture(byte[] data, Path path) {
        byte[] crashOne;
        for (int i = 0; i <256; i++) {// 256 is the max value for a byte
            crashOne= genCrashData(data,17,(byte)i);// 17 is the index byte to make the negative value for the height
            try {
                Files.write(path,crashOne);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if(testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about negative dimension");
                return;
            }
        }

    }

    /**
     * Test if the result of the execution have crashed the program
     * @param resultOfTheRun is boolean flag if the string result execution of
     *                       converter_static program containing the crash message
     * @param inputFile is the path where the test file will be generated
     * @return true if the file have been crashed the program and false otherwise
     */
    private static boolean testOnConverter(boolean resultOfTheRun, Path inputFile) {
        /* If the program is not crashing we delete the file */
        if (!resultOfTheRun) {
            try {
                Files.delete(inputFile);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", inputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }


    /**
     * Generator input file with the version initialize to the version parameter
     * @param data is a byte array with the good format for the input converter_static program
     * @param version is byte value between 0 and 100 (100 it's the largest accepted value by the converter_static program)
     * @return a byte array based on the data variable with the specific version
     */
    private static byte[] genDataWithSpecificVersion(byte[] data, byte version) {
        byte [] newData = new byte[data.length];
        System.arraycopy(data,0,newData,0,data.length);
        newData[2]= version;
        return newData;
    }

    /**
     * Generator input file with a author name with nameLength size
     * @param data is a byte array with the good format for the input converter_static program
     * @param nameLength the length of author name we want in the input file
     * @return a byte array based on the data variable with the author name of specific length
     */
    private static byte[] genDataWithBigName(byte[] data, int nameLength) {
        byte [] newData = new byte[(data.length-5)+nameLength];// 5 it's for the old size name
        System.arraycopy(data,0,newData,0,4);
        /* Creation of the new name with random value */
        for (int i = 4; i < nameLength+4 ; i++) {
            newData[i]= (byte) (Math.floor(Math.random()*255)+1); // 1 because we don't want a zero value
        }
        newData[nameLength+4]=(byte) 0x00;

        for (int i = (nameLength+4)+1, j= 10; i < newData.length && j < data.length; i++,j++) {
            newData[i]=data[j];
        }
        return newData;
    }

    /**
     * Modifier byte
     * @param data is a byte array with the good format for the input converter_static program
     * @param index of the byte will be modified
     * @param crashValue is the value that should be crashed the converter_static program
     * @return a byte array with the value at the index "index" modified by the crashValue
     */
    private static byte[] genCrashData(byte[] data, int index, byte crashValue) {
        byte[] res = new byte[data.length];
        System.arraycopy( data, 0, res, 0, data.length );
        res[index]=crashValue;
        return res;
    }

    /**
     * Simple method to modify some indexes with some byte values
     * @param data is the byte array containing a base of data for the converter progam
     * @param index is a table of indexes where one crash values are injected
     * @param crashValue is a table of crash values
     * @return a byte array with values at indexes in index table modified by values in crashValue table
     */
    private static byte[] genCrashData(byte[] data, int[] index, byte[] crashValue) {
        byte[] res = new byte[data.length];
        System.arraycopy( data, 0, res, 0, data.length );
        for (int i = 0; i < index.length; i++) {
            res[index[i]]=crashValue[i];
        }
        return res;
    }
}