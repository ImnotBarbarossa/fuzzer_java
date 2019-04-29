# lingi2347_security
A repository who regroup all the projects for the LINGI2347 course.

## How to use it
To run the program mutation-based fuzzer , use the following command:
```` 
java src/MutFuzzer testinput.img 200 100 0.01
````
- _"testinput.img"_ is the name of the correct input file,
- _200_ is the number of test runs to make,
- _100_ is the maximum number of modifications to make in one test run before giving up and starting a new test run with the correct input file,
- _0.01_ means that the fuzzer randomly changes 1% of the bytes in the input file in order to
create the next input file for the current test run. Input files that crash the Converter tool should be kept.

And to run the generation-based fuzzer, use the following command:
```
java src/GenFuzzer
```

The two programs save files that crashed the converter_static program into the own repository, fileCrashFromMutFuzzer and fileCrashFromGenFuzzer.

You can verify that the files crash the converter_static program well with the following command:
```
./converter_static fileCrashFromGenFuzzer/fileYouChoose.img testouput.img
./converter_static fileCrashFromMutFuzzer/fileYouChoose.img testouput.img
```

##About the generation-based fuzzer

We find 5 different ways to crash the converter_static program:
1. `testInputGen1.img` there is an example of the input file where the the highest byte of the height is change and when it'll be converter in signed number it's negative number. So the memory allocation will be negative and the program crash.
2. `testInputGen2.img` is the input file where 'numcolors' value in the header is too high. Actually the specification program says that it supports no more than 256 colors. However the program doesn't manage the value upper than 256 and it crash.
3. `testInputGen3.img` is the input file where we generate a name with a big size to make a buffer overflow in the converter_static program.
4. `testInputGen4.img` is the input file where we generate in the header a big positive width and a positive height that'll generate too big picture in the converter_static program and it crash.
5. `testInputGen5.img` is the input file where an old version (v20, for example) the program crash.