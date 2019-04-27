import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenFuzzer extends Fuzzer{

    public static void main(String [] args){

        byte[] data =initData();

        /* Crash about negative value for the width or the height */
        byte[] crashOne = genCrashData(data,17,(byte)0xB4);
        /* Crash about the number color is upper than 256 */
        byte[] crashTwo = genCrashData(data,21,(byte)0xF2);
        /* Crash about author name is to big */
        byte[] crashThree= genDataWithBigName(data,900);
        /* Crash about width and height too large */
        byte[] crashFour= genCrashData(data,new int[]{11,15},new byte[]{(byte) 0xFF, (byte) 0xFF});
        /* Crash about old version */
        byte[] crashFive= genDataWithSpecificVersion(data, (byte) 0x14);



        Path inputFile       = Paths.get("fileCrashFromGenFuzzer/testInputGen1.img");
        Path inputFileTwo    = Paths.get("fileCrashFromGenFuzzer/testInputGen2.img");
        Path inputFileThree  = Paths.get("fileCrashFromGenFuzzer/testInputGen3.img");
        Path inputFileFour   = Paths.get("fileCrashFromGenFuzzer/testInputGen4.img");
        Path inputFileFive   = Paths.get("fileCrashFromGenFuzzer/testInputGen5.img");

        /*Write on the file the array of bytes*/
        try {
            Files.write(inputFile, crashOne);
            Files.write(inputFileTwo, crashTwo);
            Files.write(inputFileThree, crashThree);
            Files.write(inputFileFour, crashFour);
            Files.write(inputFileFive, crashFive);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        /* Run the converter_static exe */
//        String resultOfTheRun = run_process(inputFile);
//        if (resultOfTheRun != null) {   // check if the result is not null
//            /* If the program is not crashing we delete the file */
//            if (!resultOfTheRun.equals("*** The program has crashed.")) {
//                try {
//                    Files.delete(inputFile);
//                    Files.delete(inputFileTwo);
//                    Files.delete(inputFileThree);
//                    Files.delete(inputFileFour);
//                    Files.delete(inputFileFive);
//                } catch (NoSuchFileException x) {
//                    System.err.format("%s: no such" + " file or directory%n", inputFile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    /**
     *
     * @param data
     * @param version between 0 and 100
     * @return
     */
    private static byte[] genDataWithSpecificVersion(byte[] data, byte version) {
        byte [] newData = new byte[data.length];
        System.arraycopy(data,0,newData,0,data.length);
        newData[2]= version;
        return newData;
    }

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
     * @return a byte array with modifications done
     */
    private static byte[] genCrashData(byte[] data, int[] index, byte[] crashValue) {
        byte[] res = new byte[data.length];
        System.arraycopy( data, 0, res, 0, data.length );
        for (int i = 0; i < index.length; i++) {
            res[index[i]]=crashValue[i];
        }

        return res;
    }

    private static byte[] initData() {
        File fileName = new File("testinput.img");
        return Fuzzer.read_file(fileName);
    }

}


//
//    /* Magic Number */
//    dataOne[0]=(byte)0xAB;
//    dataOne[1]=(byte)0xCD;
//
//        /* Version */
//    dataOne[2]=(byte)0x00;
//    dataOne[3]=(byte)0x64;
//
//        /* Author name */
//    dataOne[4]=(byte)0x52;
//    dataOne[5]=(byte)0x61;
//    dataOne[6]=(byte)0x6d;
//    dataOne[7]=(byte)0x69;
//    dataOne[8]=(byte)0x6e;
//    dataOne[9]=(byte)0x00;
//
//        /* Width */
//    dataOne[10]=(byte)0x00;
//    dataOne[11]=(byte)0x00;
//    dataOne[12]=(byte)0x00;
//    dataOne[13]=(byte)0x10;
//
//        /* Height*/
//    dataOne[14]=(byte)0x00;
//    dataOne[15]=(byte)0x00;
//    dataOne[16]=(byte)0x00;
//    dataOne[17]=(byte)0x10;
//
//        /* Number of colors*/
//    dataOne[18]=(byte)0x00;
//    dataOne[19]=(byte)0x00;
//    dataOne[20]=(byte)0x00;
//    dataOne[21]=(byte)0x04;
//
//        /* Color 1 */
//    dataOne[22]=(byte)0xff;
//    dataOne[23]=(byte)0x00;
//    dataOne[24]=(byte)0x00;
//    dataOne[25]=(byte)0x00;
//
//        /* Color 2 */
//    dataOne[26]=(byte)0x00;
//    dataOne[27]=(byte)0xff;
//    dataOne[28]=(byte)0x00;
//    dataOne[29]=(byte)0xff;
//
//        /* Color 3 */
//    dataOne[30]=(byte)0x00;
//    dataOne[31]=(byte)0x00;
//    dataOne[32]=(byte)0x00;
//    dataOne[33]=(byte)0xff;
//
//        /* Color 4 */
//    dataOne[34]=(byte)0x00;
//    dataOne[35]=(byte)0x00;
//    dataOne[36]=(byte)0xff;
//    dataOne[37]=(byte)0x00;
//
//        for (int i = 41; i < dataOne.length; i++)
//    dataOne[i]= (byte) Math.floor(Math.random()*4);
//
