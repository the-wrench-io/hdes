package io.resys.hdes.compiler.spi.java.visitors;

import java.util.Collection;

/*-
 * #%L
 * hdes-compiler
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.function.Function;

import javax.lang.model.element.Modifier;

import org.immutables.value.Value.Immutable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DirectionType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Headers;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtMethodSpec;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtTypesSpec;

public class DtAstNodeVisitorJavaInterface extends DtAstNodeVisitorTemplate<DtJavaSpec, TypeSpec> {

  private DecisionTableBody body;
  
  @Override
  public TypeSpec visitDecisionTableBody(DecisionTableBody node) {
    this.body = node;
    com.squareup.javapoet.TypeName returnType = ClassName.get("", JavaNaming.dtOutput(node.getId()));
    if(body.getHitPolicy() instanceof HitPolicyAll) {
      returnType = ParameterizedTypeName.get(ClassName.get(Collection.class), returnType);
    }
    
    TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(node.getId())
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(Function.class), 
            ClassName.get("", JavaNaming.dtInput(node.getId())),
            returnType))
        .addTypes(visitHeaders(node.getHeaders()).getValues());
    
    return interfaceBuilder.build();
  }
  
  @Override
  public DtTypesSpec visitHeaders(Headers node) {
    Function<String, TypeSpec.Builder> from = (name) -> TypeSpec
        .interfaceBuilder(name)
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    
    TypeSpec.Builder inputBuilder = from.apply(JavaNaming.dtInput(body.getId()));
    TypeSpec.Builder outputBuilder = from.apply(JavaNaming.dtOutput(body.getId()));
    
    for(Header header : node.getValues()) {
      MethodSpec method = visitHeader(header).getValue();
      if(header.getDirection() == DirectionType.IN) {
        inputBuilder.addMethod(method);
      } else {
        outputBuilder.addMethod(method);        
      }
    }
    return ImmutableDtTypesSpec.builder()
        .addValues(inputBuilder.build(), outputBuilder.build())
        .build();
  }

  @Override
  public DtMethodSpec visitHeader(Header node) {
    MethodSpec method = MethodSpec.methodBuilder(JavaNaming.getMethod(node.getName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(JavaNaming.type(node.getType()))
        .build();
    return ImmutableDtMethodSpec.builder().value(method).build();
  }
}
