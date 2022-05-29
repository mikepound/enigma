import com.mikepound.enigma.Enigma;
import com.mikepound.enigma.Plugboard;
import com.mikepound.enigma.Reflector;
import com.mikepound.enigma.RotorType;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EnigmaTest {

    @Test
    void encryptTest() {
        // Basic settings
        Enigma e = new Enigma(new RotorType[]{RotorType.I, RotorType.II, RotorType.III}, Reflector.B, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new Plugboard(""));
        String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZAAAAAAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = "BJELRQZVJWARXSNBXORSTNCFMEYHCXTGYJFLINHNXSHIUNTHEORXOPLOVFEKAGADSPNPCMHRVZCYECDAZIHVYGPITMSRZKGGHLSRBLHL";
        char[] ciphertext = e.encrypt(input.toCharArray());
        assertArrayEquals(output.toCharArray(), ciphertext);

        // Varied rotors
        e = new Enigma(new RotorType[]{RotorType.VII, RotorType.V, RotorType.IV}, Reflector.B, new int[]{10, 5, 12}, new int[]{1, 2, 3}, new Plugboard(""));
        ciphertext = e.encrypt(input.toCharArray());
        output = "FOTYBPKLBZQSGZBOPUFYPFUSETWKNQQHVNHLKJZZZKHUBEJLGVUNIOYSDTEZJQHHAOYYZSENTGXNJCHEDFHQUCGCGJBURNSEDZSEPLQP";
        assertArrayEquals(output.toCharArray(), ciphertext);

        // Long input
        e = new Enigma(new RotorType[]{RotorType.III, RotorType.VI, RotorType.VIII}, Reflector.B, new int[]{3, 5, 9}, new int[]{11, 13, 19}, new Plugboard(""));
        char[] longInput = new char[500];
        for (int i = 0; i < 500; i++) longInput[i] = 'A';
        ciphertext = e.encrypt(longInput);
        output = "YJKJMFQKPCUOCKTEZQVXYZJWJFROVJMWJVXRCQYFCUVBRELVHRWGPYGCHVLBVJEVTTYVMWKJFOZHLJEXYXRDBEVEHVXKQSBPYZN" +
                "IQDCBGTDDWZQWLHIBQNTYPIEBMNINNGMUPPGLSZCBRJULOLNJSOEDLOBXXGEVTKCOTTLDZPHBUFKLWSFSRKOMXKZELBDJNRUDUCO" +
                "TNCGLIKVKMHHCYDEKFNOECFBWRIEFQQUFXKKGNTSTVHVITVHDFKIJIHOGMDSQUFMZCGGFZMJUKGDNDSNSJKWKENIRQKSUUHJYMIG" +
                "WWNMIESFRCVIBFSOUCLBYEEHMESHSGFDESQZJLTORNFBIFUWIFJTOPVMFQCFCFPYZOJFQRFQZTTTOECTDOOYTGVKEWPSZGHCTQRP" +
                "GZQOVTTOIEGGHEFDOVSUQLLGNOOWGLCLOWSISUGSVIHWCMSIUUSBWQIGWEWRKQFQQRZHMQJNKQTJFDIJYHDFCWTHXUOOCVRCVYOHL";
        assertArrayEquals(output.toCharArray(), ciphertext);
    }

    @Test
    void decryptTest() {
        Random rand = new Random();
        RotorType[] allRotors = new RotorType[]{RotorType.I, RotorType.II, RotorType.III, RotorType.IV, RotorType.V, RotorType.VI, RotorType.VII, RotorType.VIII};

        char[] input = new char[1000];
        for (int i = 0; i < 1000; i++) {
            input[i] = (char) (rand.nextInt(26) + 65);
        }

        for (int test = 0; test < 10; test++) {
            // Random initialisation
            RotorType[] rotors = new RotorType[]{allRotors[rand.nextInt(8)],
                    allRotors[rand.nextInt(8)],
                    allRotors[rand.nextInt(8)]};

            int[] startingPositions = new int[]{rand.nextInt(26), rand.nextInt(26), rand.nextInt(26)};
            int[] ringSettings = new int[]{rand.nextInt(26), rand.nextInt(26), rand.nextInt(26)};

            // Machine 1 - Encryption
            Enigma e = new Enigma(rotors, Reflector.B, startingPositions, ringSettings, new Plugboard(""));
            char[] ciphertext = e.encrypt(input);

            // Machine 2 - Decryption
            Enigma e2 = new Enigma(rotors, Reflector.B, startingPositions, ringSettings, new Plugboard(""));
            char[] plaintext = e2.encrypt(ciphertext);

            assertArrayEquals(input, plaintext);
        }

    }

    @Test
    void plugboardTest() {
        // Simple test - 4 plugs
        Enigma e = new Enigma(new RotorType[]{RotorType.I, RotorType.II, RotorType.III}, Reflector.B, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new Plugboard("AC FG JY LW"));
        char[] input = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".toCharArray();
        char[] output = e.encrypt(input);
        char[] expectedOutput = "QREBNMCYZELKQOJCGJVIVGLYEMUPCURPVPUMDIWXPPWROOQEGI".toCharArray();
        assertArrayEquals(expectedOutput, output);

        // 6 plugs
        e = new Enigma(new RotorType[]{RotorType.IV, RotorType.VI, RotorType.III}, Reflector.B, new int[]{0, 10, 6}, new int[]{0, 0, 0}, new Plugboard("BM DH RS KN GZ FQ"));
        input = "WRBHFRROSFHBCHVBENQFAGNYCGCRSTQYAJNROJAKVKXAHGUZHZVKWUTDGMBMSCYQSKABUGRVMIUOWAPKCMHYCRTSDEYTNJLVWNQY".toCharArray();
        expectedOutput = "FYTIDQIBHDONUPAUVPNKILDHDJGCWFVMJUFNJSFYZTSPITBURMCJEEAMZAZIJMZAVFCTYTKYORHYDDSXHBLQWPJBMSSWIPSWLENZ".toCharArray();
        output = e.encrypt(input);
        assertArrayEquals(expectedOutput, output);

        // 10 plugs
        e = new Enigma(new RotorType[]{RotorType.I, RotorType.II, RotorType.III}, Reflector.B, new int[]{0, 1, 20}, new int[]{5, 5, 4}, new Plugboard("AG HR YT KI FL WE NM SD OP QJ"));
        input = "RNXYAZUYTFNQFMBOLNYNYBUYPMWJUQSBYRHPOIRKQSIKBKEKEAJUNNVGUQDODVFQZHASHMQIHSQXICTSJNAUVZYIHVBBARPJADRH".toCharArray();
        expectedOutput = "CFBJTPYXROYGGVTGBUTEBURBXNUZGGRALBNXIQHVBFWPLZQSCEZWTAWCKKPRSWOGNYXLCOTQAWDRRKBCADTKZGPWSTNYIJGLVIUQ".toCharArray();
        output = e.encrypt(input);
        assertArrayEquals(expectedOutput, output);
    }
}
