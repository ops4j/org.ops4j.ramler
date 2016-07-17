/*
 * Copyright 2016 OPS4J Contributors
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
package org.ops4j.ramler.generator;

import org.ops4j.ramler.generator.ApiVisitor;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingApiVisitor implements ApiVisitor {

    private static Logger log = LoggerFactory.getLogger(LoggingApiVisitor.class);

    
    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        log.info("object type: name = {}, type = {}", type.name(), type.type());
    }
    
    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (property instanceof ObjectTypeDeclaration) {
            ObjectTypeDeclaration obj = (ObjectTypeDeclaration) property;
            log.info("prop={}, type={}", obj.name(), obj.type());
        }
        else {
            log.info("prop={}", property.name());            
        }
    }
    
    @Override
    public void visitMethodStart(Method method) {
        Response response = method.responses().get(0);
        if (response.body().isEmpty()) {
            return;
        }
        TypeDeclaration body = response.body().get(0);
        log.info("body = {}", body.type());
    }
}
