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
package org.ops4j.ramler.model;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

/**
 * Access methods for annotation values.
 *
 * @author Harald Wellmann
 */
public class Annotations {

    private Annotations() {
        // hidden utility class constructor
    }

    /**
     * Gets the value list of a string valued annotation of a given name on the given type.
     *
     * @param decl
     *            type declaration
     * @param name
     *            name of annotation with value type {@code string[]}
     * @return list of annotation values (never null)
     */
    public static List<String> getStringAnnotations(TypeDeclaration decl, String name) {
        return annotationsByName(decl, name).flatMap(a -> findStringAnnotationValues(a))
            .collect(toList());
    }

    private static Stream<String> findStringAnnotationValues(AnnotationRef ref) {
        TypeInstanceProperty tip = ref.structuredValue().properties().get(0);
        return tip.values().stream().map(ti -> ti.value()).map(String.class::cast);
    }

    private static String findStringAnnotationValue(AnnotationRef ref) {
        return (String) ref.structuredValue().value();
    }

    /**
     * Find all annotations with the given name on the given declaration.
     *
     * @param decl
     *            declaration
     * @param name
     *            annotation name
     * @return stream of matching annotations
     */
    public static Stream<AnnotationRef> annotationsByName(Annotable decl, String name) {
        return decl.annotations().stream().filter(a -> a.annotation().name().equals(name));
    }

    /**
     * Finds the value of the {@code (codeName)} annotation on the given declaration.
     *
     * @param decl
     *            declaration
     * @return annotation value, or null if annotation not present
     */
    public static String findCodeName(Annotable decl) {
        return annotationsByName(decl, "codeName").findFirst()
            .map(Annotations::findStringAnnotationValue).orElse(null);
    }

    /**
     * Finds the value of the {@code (typeVar)} annotation on the given declaration.
     *
     * @param decl
     *            declaration
     * @return annotation value, or null if annotation not present
     */
    public static String findTypeVar(Annotable decl) {
        return annotationsByName(decl, "typeVar").findFirst()
            .map(Annotations::findStringAnnotationValue).orElse(null);
    }

    /**
     * Checks if the given declaration has an {@code (id)} annotation.
     *
     * @param decl
     *            declaration
     * @return true if annotation is present
     */
    public static boolean isIdentity(Annotable decl) {
        return annotationsByName(decl, "id").findFirst().isPresent();
    }

    public static boolean isPreserveNull(Annotable decl) {
        return annotationsByName(decl, "preserveNull").findFirst().isPresent();
    }

}
