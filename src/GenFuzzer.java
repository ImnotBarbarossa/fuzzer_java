import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenFuzzer extends Fuzzer{

    public static void main(String [] args){

        byte[] data =initData();

        /* Crash about negative value for the width or the height */
        System.out.println("===== Test negative picture size =====");
        negativeDimensionPicture(data, Paths.get("fileCrashFromGenFuzzer/testInputGen1.img"));

        /* Crash about the number color is upper than 256 */
        System.out.println("===== Test upper 256 value for the number color =====");
        testOnNumberColor(data, Paths.get("fileCrashFromGenFuzzer/testInputGen2.img"));

        /* Crash about author name is to big */
        System.out.println("===== Test big author name =====");
        testOnTheAuthorName(data,Paths.get("fileCrashFromGenFuzzer/testInputGen3.img"));

        /* Crash about width and height too large */
        System.out.println("===== Test huge picture size =====");
        testOnTheHugeDimension(data,Paths.get("fileCrashFromGenFuzzer/testInputGen4.img"));

        /* Crash about old version */
        System.out.println("===== Test old version =====");
        testOnTheOldVersion(data,Paths.get("fileCrashFromGenFuzzer/testInputGen5.img"));

    }

    private static void testOnTheOldVersion(byte[] data, Path path) {
        byte [] crashData;
        for (int i = 0; i < 100; i++) {
            crashData= genDataWithSpecificVersion(data, (byte) i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about an old version : v-"+i);
                break;
            }
        }
    }
    /* ICI TU SAIS VERIFIER */
    private static void testOnTheHugeDimension(byte[] data, Path path) {
        byte [] crashData;
        int [] hexaIndex = new int[]{11,15};
        for (int i = 0; i < 255; i++) {
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte) i, (byte) i});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about huge picture dimension");
                break;
            }
            System.out.println("i:"+i);
        }
    }


    private static void testOnTheAuthorName(byte[] data, Path path) {
        byte [] crashData;
        for (int i = 20; i < 1000; i+=10) {
            crashData= genDataWithBigName(data,i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND]: Crash about the author name with length: " + i);
                break;
            }
        }
    }

    private static void testOnNumberColor(byte[] data, Path path) {
        byte[] crashData;
        for (int i = 0; i < 255; i++) {
            crashData=genCrashData(data,21,(byte)i);
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about the color number upper than 256");
                break;
            }
        }
    }

    private static void negativeDimensionPicture(byte[] data, Path inputFile) {
        byte[] crashOne;
        for (int i = 0; i <255; i++) {
           crashOne= genCrashData(data,17,(byte)i);
            try {
                Files.write(inputFile,crashOne);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if(testOnConverter(run_process(inputFile),inputFile)){
                System.out.println("[FOUND]: Crash about negative dimension");
                break;
            }
        }

    }

    private static boolean testOnConverter(String resultOfTheRun, Path inputFile) {
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
                return false;
            }
            return true;
        }
        return false;
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
