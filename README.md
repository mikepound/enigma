# Java Enigma

This is a Java implementation of an Enigma machine, along with code that attempts to break the encryption. This code is associated with the Computerphile video on [cracking enigma](https://www.youtube.com/watch?v=RzWB5jL5RX0).

An enigma machine is a mechanical encryption device that saw a lot of use before and during WW2. This code simulates a 3 rotor enigma, including the 8 rotors commonly seen during the war. 

## Installing and Usage
You can compile and run this code yourself if you have Java installed. For convenience I recommend using [IntelliJ](https://www.jetbrains.com/idea/) or a similar IDE. The community edition is free, and it'll make editing, compiling and running the code a lot easier. If you'd like to run it yourself, first install java, then follow the instructions below. This assumes you've installed java and git.

### Windows
Clone and traverse into the enigma directory
```
git clone https://github.com/mikepound/enigma.git
cd enigma
```

Compile all the java files from src into bin
```
javac -d bin -sourcepath src src\com\mikepound\Main.java
```

Copy the n-gram statistics into the bin folder too
```
xcopy resources\data bin\data\
```

Run the Enigma code in main
```
java -cp bin com.mikepound.Main
```

### Linux/Unix
Clone and traverse into the enigma directory
```
git clone https://github.com/mikepound/enigma.git
cd enigma
```

Compile all the java files from src into bin
```
javac -d bin -sourcepath src src/com/mikepound/Main.java
```

Copy the n-gram statistics into the bin folder too
```
cp -r resources/data bin/data
```

Run the Enigma code in main
```
java -cp bin com.mikepound.Main
```

## The Enigma Machine
The code for the enigma machine can be found in the `enigma` package. In the `analysis` package is the code to perform attacks on ciphertext. The attack uses various fitness functions that attempt to measure the effectiveness of a test decryption, found within the `analysis.fitness` package. Finally, the `Main.java` file is where you'll find the actual attack I performed in the video, and so it also contains a lot of examples you can use to run your own attacks.

### Creating a Java Enigma
The code itself is fairly straightforward. You can create a new enigma machine using a constructor, for example this code will create a new object called `enigmaMachine` with the settings provided:

```java
enigmaMachine = new Enigma(new String[] {"VII", "V", "IV"}, "B", new int[] {10,5,12}, new int[] {1,2,3}, "AD FT WH JO PN");
```

Rotors and the reflector are given by their common names used in the war, with rotors labelled as `"I"` through to `"VIII"`, and reflectors `"B"` and `"C"`. I've not implemented every variant, such as the thin reflectors seen in naval 4-rotor enigma. You could easily add these if you liked. Starting positions and ring settings are given as integers 0-25 rather than the A-Z often seen, this is just to avoid unnecessary mappings. The majority of the code here treats letters as 0-25 to aid indexing. Plugs are given as a string of character pairs representing steckered partners. If you don't wish to use a plugboard, `""` or `null` is fine.

### Encrypting and Decrypting
Given an enigma instance like the `enigmaMachine` above, encryption or decryption is performed on character arrays of capital letters [A-Z]. Simply to save time I\'ve not done a lot of defensive coding to remove invalid characters, so be careful to only use uppercase, or to strip unwanted characters out of strings. Here is an encryption example using the enigma machine above:

```java
char[] plaintext = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
char[] ciphertext = enigmaMachine.encrypt(plaintext);
String s = new String(ciphertext); // UJFZBOKXBAQSGCLDNUTSNTASEF
```
You can quickly check everything is working by running the tests found in the `EnigmaTest.java` file.

### How it works
Throughout the enigma machine, letters A-Z are represented as integers 0-25. Most of the components, the rotors, reflector and plugboard are treated as arrays that map values 0-25 to a different set of values 0-25. Encrypting or decrypting is simply a case of passing a value through these arrays in turn. What makes enigma trickier is that the arrays rotate, and that they can have different starting or ring positions. For efficiency in this implementation I keep the arrays fixed, and simulate rotation by shifting the index in and out of each rotor. Before each character is encrypted the rotors rotate, sometimes causing the neighbouring rotors to also rotate, this is handled by the `rotate()` function. Enigma has a quirk whereby the middle rotors moves twice when it turns the left-most rotor. This is called double stepping, and is also implemented here.

## Breaking a Code
Breaking an enigma message here comes down to decrypting a ciphertext with all possible rotor configurations and seeing which output looks the best. We measure best here using a fitness function.

### Fitness functions
The code makes a number of fitness functions available that can be used to measure how close a test decryption is to English text. Each works similarly, some work better than others. You can test to see which work best for a given message. The fitness functions are:
* **Index of coincidence**. The probability of any random two letters being identical. Tends to be higher for proper sentences than for random encrypted text. I've found this is quite good as an initial fitness function when there are many plugs involved.
* **Single / Bi / Tri / Quad grams**. The probability of a sentence measured based on the probability of constituent sequences of characters. Bigrams are pairs, such as AA or ST. Trigrams are triplets, e.g. THE, and so on. The more letters you use, e.g. single -> bi -> tri -> quad seems to improve the power of the fitness function, but you can't rely on this. I've found the longer measures are better when you already have some of the settings correct.
* **Plaintext Fitness**. This function is a known plaintext attack, comparing the decryption against all or portions of a suspected real plaintext. This is by far the most effective solution, even a few words of known plaintext will substantially increase your odds of a break even with a number of plugboard swaps. The constructor for this fitness function has two possible constructors:
```java
public KnownPlaintextFitness(char[] plaintext)
```
Use this if you have an entire complete plaintext you're looking for.

```java
public KnownPlaintextFitness(String[] words, int[] offsets)
```
This one takes pairs of words and their possible positions within the plaintext. For example, in the string "tobeornottobethatisthequestion" you might supply {"to", "that", "question"} and {0, 13, 22}. This function is used if you can guess some words, but aren't sure of the whole sentence, such as when you have partially broken the message already. Note that in the default example known plaintext won't improve much, because the attack is already successful. The errors in the output are not due to the fitness function, rather that we are not simultaneously pairing rotors and ring settings.

### Ciphertext Analysis
The basic approach to the attack is as follows:
1. Decrypt the ciphertext with every possible rotor in each position, and rotated to each starting position. All rotor ring settings are set to 0. No plugboard. For each decryption, measure the text fitness using one of the available fitness functions. Save the best performing rotor configuration.
2. Fix the rotors, and iterate through all possible ring settings for the middle and right rotors, again testing the fitness of the decryption. You do not have to use the same fitness function as before.
3. Fix all settings, and then use a hill climbing approach to find the best performing plugboard swaps, again measured using a fitness function.

## Notes
* The code is fairly efficient, Enigma boils down to a lot of array indexing into different rotors. This said, I didn't worry too much about speed, it's plenty fast enough. I used classes and functions rather than doing things inline, for example. Modern compilers will optimise a lot of it anyway.
* I've added a more optimised and multi-threaded version in a branch called [optimised](https://github.com/mikepound/enigma/tree/optimised). I've managed to get the code to break a message in under 4 seconds on one of our servers! I'm keeping this code separate to the main branch to keep the main branch clean and based off the original video.
* Similarly, in the brute force key search code, for simplicity I create new enigma machines as required rather than implementing a number of specific reinitialisation functions that would be faster.
* I've not written any kind of command line parsing here. You're welcome to add this, but i felt for a tutorial on enigma and breaking it, a step by step procedure in main was fine.

## Resources
1. For more details on enigma, the [wikipedia articles](https://en.wikipedia.org/wiki/Enigma_machine) are a great resource.

2. This attack is a variant of that originally proposed by James Gillogly. His work on this is still available via the web archive [here](https://web.archive.org/web/20060720040135/http://members.fortunecity.com/jpeschel/gillog1.htm).

3. If you'd like a more visual example of both encryption and cracking enigma codes, the Cryptool project is a great tool for this. [Cryptool 2](https://www.cryptool.org/en/) has a great visualiser and can run cracking code similar to my own. I used cryptool to write the tests I used to make sure my own enigma implementation was working.
