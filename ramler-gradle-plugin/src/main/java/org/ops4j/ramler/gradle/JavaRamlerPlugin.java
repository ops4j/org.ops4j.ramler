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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class JavaRamlerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks()
            .withType(RamlerJavaGenerator.class, task -> {
                project.getTasks()
                    .getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    .dependsOn(task);
            });

        project.getTasks()
            .withType(RamlerJavaTestGenerator.class, task -> {
                project.getTasks()
                    .getByName(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME)
                    .dependsOn(task);
            });

    }
}
