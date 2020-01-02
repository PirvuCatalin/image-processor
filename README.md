#ImageProcessor

The ImageProcessor is a small project for my 3rd year course 
"Web Applications using Java" of the Faculty of Automatic Control and Computer Science, Politehnica University of Bucharest.

This is meant to teach us concepts of OOP, that's why you can see the extra-abstractization of classes.

<b>This application transforms an input 24bit RGB BMP image file from grayscale to binary.</b>

Under the folder [/out](out), the .class files, the .jar and javadoc will be kept updated for the ease of use.
Of course they can also be manually generated with the sources.

To test this, you can use the images under [/test](out/test) folder 
(they also contain invalid images, for the sake of testing) and follow the steps: 
```
1. Clone this repository
2. Open a terminal (MacOS / Linux) or a CMD
3. Navigate to /out/artifacts/ImageProcessor_jar
4. Execute: java -jar ImageProcessor.jar -P "../../test"

You can also use the classes files, just navigate to /out/production/ImageProcessor 
and execute java com.cpirvu.ImageProcessor -P "../../test".
```

For the input arguments, you can use the following:
```
    - -P <path>, the path to the image file or directory of image files
    - [-M <numberOfThreads>], instructs the application to use up to numberOfThreads threads, if the file is a directory (1-255, default is 5).
    - [-T <threshold>], change the static threshold used in binarization algorithm (0-255). Default is 127.
    - [-F], "force" meaning to first convert the 24bit BMP to grayscale if needed
```

For more details regarding the usage, you can use `java -jar ImageProcessor.jar help`.


A list of other nice-to-have modules can be found at the end of [AWJ_Homework](AWJ_Homework.pdf) file.