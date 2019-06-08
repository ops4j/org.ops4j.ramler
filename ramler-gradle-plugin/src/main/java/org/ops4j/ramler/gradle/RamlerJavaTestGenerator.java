/*
 * Copyright 2019 OPS4J Contributors
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
package org.ops4j.ramler.gradle;

import org.gradle.api.tasks.SourceSet;

/**
 * Tasks for generating Java sources to be included in the {@code test} source set for the
 * {@code compileTestJava} task,
 *
 * @author Harald Wellmann
 */
public class RamlerJavaTestGenerator extends AbstractRamlerJavaGenerator {

    @Override
    protected String getDefaultOutputSubdir() {
        return "generated-test-sources";
    }

    @Override
    protected String getSourceSet() {
        return SourceSet.TEST_SOURCE_SET_NAME;
    }
}
