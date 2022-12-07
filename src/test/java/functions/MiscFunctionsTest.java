package functions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MiscFunctionsTest {
    private static final String fileName = "src/test/resources/testFile.txt";
    @BeforeEach
    void setUp() throws IOException {
        File file = new File(fileName);

        FileWriter fileWriter = new FileWriter(file, false);

        fileWriter.write("""
                The first line
                The second line
                A third line
                """);
        fileWriter.close();
    }

    @Test
    void getLastLineOfFile() {
        try {
            assertEquals("A third line", MiscFunctions.getLastLineOfFile(fileName).orElseThrow());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test void deleteLastLineOfFile() {
        try {
            MiscFunctions.deleteLastLineOfFile(fileName);
            assertEquals("The second line", MiscFunctions.getLastLineOfFile(fileName).orElseThrow());
        } catch (IOException e) {
            fail(e);
        }
    }

}