/*
 *    Copyright 2022 Glenn Mamacos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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