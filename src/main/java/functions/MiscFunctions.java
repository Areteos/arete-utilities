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


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Optional;

@SuppressWarnings("unused")
public final class MiscFunctions {
    private MiscFunctions() {}

    /**
     * Finds and returns the last line of the file. Will return empty if the file is empty. Note that a newline at the
     * end of a file will be skipped.
     * @param filename The filename from which to read the last line
     * @return The last line of the file
     * @throws IOException If the file does not exist or otherwise cannot be accessed
     */
    public static Optional<String> getLastLineOfFile(String filename) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        long length = file.length() - 1;
        if (length < 0) {
            return Optional.empty();
        }

        while (length > 0) {
            file.seek(--length);
            if (file.readByte() == 10) {
                length++;
                break;
            }
        }
        file.seek(length);

        String line = file.readLine();
        file.close();
        return Optional.ofNullable(line);
    }


    /**
     * Deletes the last line of the given file. Note that a newline at the end of a file will be skipped.
     * @param filename The file to delete the last line of
     * @throws IOException If the file does not exist
     */
    public static void deleteLastLineOfFile(String filename) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        long length = file.length() - 1;
        if (length < 0) {
            return;
        }

        while (length > 0) {
            file.seek(--length);
            if (file.readByte() == 10) {
                length++;
                break;
            }
        }

        file.setLength(length);
        file.close();
    }

    /**
     * Given some number input, return a String representation with all redundant zeros stripped away. For example, 5.0
     * becomes 5
     * @param input The number from which to strip zeros
     * @return A string representation of the input without any redundant zeros
     */
    public static String stripTrailingZeros(double input) {
        String stringInput = String.valueOf(input);
        return stringInput.contains(".") ? stringInput.replaceAll("0*$","").replaceAll("\\.$","") : stringInput;
    }
}