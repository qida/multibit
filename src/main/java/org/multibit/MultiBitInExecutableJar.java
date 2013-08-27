/**
 * Copyright 2011 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Main MultiBit entry class for when running in an executable jar - put console
 * output to a file.
 */
public final class MultiBitInExecutableJar {

    public static final String OUTPUT_DIRECTORY = "log";
    public static final String CONSOLE_OUTPUT_FILENAME = "multibit.log";

    private static Logger log = LoggerFactory.getLogger(MultiBitInExecutableJar.class);

    static {
        java.awt.Toolkit.getDefaultToolkit();
    }

    /**
     * Hidden constructor
     */
    private MultiBitInExecutableJar() {
    }

    /**
     * Start multibit user interface when running in a jar.
     * This will adjust the logging framework output to ensure that console output is sent
     * to a file appender in the client.
     *
     * @param args The optional command line arguments ([0] can be a Bitcoin URI
     */
    public static void main(String[] args) {
        // Redirect the console output to a file.
        PrintStream fileStream;
        try {
            // Get the current data directory.
            ApplicationDataDirectoryLocator applicationDataDirectoryLocator = new ApplicationDataDirectoryLocator();

            String outputDirectory;
            String consoleOutputFilename;

            if ("".equals(applicationDataDirectoryLocator.getApplicationDataDirectory())) {
                // Use defaults.
                outputDirectory = OUTPUT_DIRECTORY;
                consoleOutputFilename = OUTPUT_DIRECTORY + File.separator + CONSOLE_OUTPUT_FILENAME;
            } else {
                // Use defined data directory as the root.
                outputDirectory = applicationDataDirectoryLocator.getApplicationDataDirectory() + File.separator
                        + OUTPUT_DIRECTORY;
                consoleOutputFilename = applicationDataDirectoryLocator.getApplicationDataDirectory() + File.separator
                        + OUTPUT_DIRECTORY + File.separator + CONSOLE_OUTPUT_FILENAME;
            }

            log = LoggerFactory.getLogger(MultiBitInExecutableJar.class);

            // Create output directory.
            (new File(outputDirectory)).mkdir();

            // Create output console log.
            (new File(consoleOutputFilename)).createNewFile();

            fileStream = new PrintStream(new FileOutputStream(consoleOutputFilename, true));

            if (fileStream != null) {
                // Redirecting console output to file .
                System.setOut(fileStream);
                // Redirecting runtime exceptions to file.
                System.setErr(fileStream);
            }
        } catch (FileNotFoundException e) {
            if (log != null) {
                log.error("Error in IO Redirection", e);
            } else {
                System.out.println("MultiBit start up : Error in IO Redirection : " + e.getClass().getCanonicalName() + " " + e.getMessage());
            }
        } catch (Exception e) {
            if (log != null) {
                log.error("Error in redirecting output & exceptions to file", e);
            } else {
                System.out.println("MultiBit start up : Error in redirecting output & exceptions to file : " + e.getClass().getCanonicalName() + " " + e.getMessage());
            }
        } finally {
            // Call the main MultiBit code.
            MultiBit.main(args);
        }
    }
}
