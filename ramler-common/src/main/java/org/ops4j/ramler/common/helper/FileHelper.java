/*
 * Copyright 2017 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.ramler.common.helper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.ops4j.ramler.common.exc.GeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for file system operations.
 *
 * @author Harald Wellmann
 *
 */
public class FileHelper {

    private static Logger log = LoggerFactory.getLogger(FileHelper.class);

    private FileHelper() {
        // hidden utility class constructor
    }

    /**
     * Checks is the given directory exists and creates it otherwise.
     *
     * @param dir required directory
     */
    public static void createDirectoryIfNeeded(File dir) {
        boolean success;
        if (dir.exists()) {
            success = dir.isDirectory();
        }
        else {
            success = dir.mkdirs();
        }
        if (!success) {
            throw new GeneratorException("could not create " + dir);
        }
    }

    /**
     * Writes the given content to the file with the given name in the target directory.
     *
     * @param content
     *            string content
     * @param file
     *            output file
     */
    public static void writeToFile(String content, File file) {
        log.debug("generating {}\n{}", file, content);
        try {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }
}
