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
package org.ops4j.ramler.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class Version {

    private static Logger log = LoggerFactory.getLogger(JavaGeneratorContext.class);

    private static final String POM_PROPERTIES = "/META-INF/maven/org.ops4j.ramler/ramler-java/pom.properties";

    private static String ramlerVersion;

    private Version() {
        // hidden utility class constructor
    }

    public static synchronized String getRamlerVersion() {
        if (ramlerVersion == null) {
            Properties props = new Properties();
            try (InputStream is = JavaGeneratorContext.class.getResourceAsStream(POM_PROPERTIES)) {
                // The resource may not be available when running from the IDE
                if (is != null) {
                    props.load(is);
                }
            }
            catch (IOException exc) {
                log.debug("Error loading pom.properties", exc);
            }
            ramlerVersion = props.getProperty("version", "UNKNOWN");
        }
        return ramlerVersion;
    }
}
