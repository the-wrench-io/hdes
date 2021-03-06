package io.resys.hdes.compiler.spi.st.visitors;

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

import java.util.function.Consumer;

import org.immutables.value.Value;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.BodyNode.ContextTypeDef;
import io.resys.hdes.ast.api.nodes.BodyNode.Headers;
import io.resys.hdes.ast.api.nodes.BodyNode.ObjectDef;
import io.resys.hdes.ast.api.nodes.BodyNode.ScalarDef;
import io.resys.hdes.ast.api.nodes.BodyNode.TypeDef;
import io.resys.hdes.ast.api.nodes.HdesTree;
import io.resys.hdes.ast.api.nodes.HdesTree.ServiceTree;
import io.resys.hdes.ast.api.nodes.InvocationNode;
import io.resys.hdes.ast.api.nodes.MappingNode.ObjectMappingDef;
import io.resys.hdes.ast.api.nodes.ServiceNode.CommandInvocation;
import io.resys.hdes.ast.api.nodes.ServiceNode.ServicePromise;
import io.resys.hdes.ast.api.visitors.ServiceBodyVisitor;
import io.resys.hdes.compiler.spi.spec.HdesDefSpec;
import io.resys.hdes.compiler.spi.spec.ImmutableSpec;
import io.resys.hdes.compiler.spi.units.CompilerNode.CompilerType;
import io.resys.hdes.compiler.spi.units.CompilerNode.ServiceUnit;

public class StApiVisitor implements ServiceBodyVisitor<StSpec, TypeSpec> {

  @Value.Immutable
  public interface StHeaderSpec extends StSpec {
    Consumer<ImmutableSpec.ImmutableBuilder> getValue();
    Consumer<HdesDefSpec.ApiBuilder> getNested();
  }

  @Value.Immutable
  public interface StHeadersSpec extends StSpec {
    Consumer<HdesDefSpec.ApiBuilder> getValue();
  }
  
  @Override
  public TypeSpec visitBody(ServiceTree ctx) {
    final var unit = ctx.get().node(ServiceUnit.class);
    final CompilerType compilerType = unit.getType();
    final HdesDefSpec.ApiBuilder api = HdesDefSpec.api(compilerType);
    
    // create input/output
    visitHeaders(ctx.getValue().getHeaders(), ctx).getValue().accept(api);
    
    return api.build().build();
  }

  @Override
  public StHeadersSpec visitHeaders(Headers node, HdesTree ctx) {
    HdesTree next = ctx.next(node);
    Consumer<HdesDefSpec.ApiBuilder> consumer = (api) -> {
      ImmutableSpec.ImmutableBuilder input = api.inputValue();
      ImmutableSpec.ImmutableBuilder output = api.outputValue();
      
      for(TypeDef typeDef : node.getAcceptDefs()) {
        StHeaderSpec header = visitHeader(typeDef, next);
        header.getValue().accept(input);
        header.getNested().accept(api);
      }
      
      for(TypeDef typeDef : node.getReturnDefs()) {
        StHeaderSpec header = visitHeader(typeDef, next);
        header.getValue().accept(output);
        header.getNested().accept(api);
      }
    
      input.build();
      output.build();
    };
    return ImmutableStHeadersSpec.builder().value(consumer).build();
  }

  @Override
  public StHeaderSpec visitHeader(TypeDef node, HdesTree ctx) {
    if(node instanceof ScalarDef) {
      return visitHeader((ScalarDef) node, ctx);
    }
    return visitHeader((ObjectDef) node, ctx);
  }
  
  @Override
  public StHeaderSpec visitHeader(ScalarDef node, HdesTree ctx) {
    return ImmutableStHeaderSpec.builder()
        .value((immutable) -> immutable.method(node).build())
        .nested((api) -> {}).build();
  }

  @Override
  public StHeaderSpec visitHeader(ObjectDef node, HdesTree ctx) {
    final var unit = ctx.get().node(ServiceUnit.class);
    
    Consumer<HdesDefSpec.ApiBuilder> nested = (api) -> {
      final ImmutableSpec.ImmutableBuilder immutable;
      
      if(node.getContext() == ContextTypeDef.ACCEPTS) {
        immutable = api.inputValue(unit.getAccepts(node).getName());
      } else {
        immutable = api.outputValue(unit.getReturns(node).getName());
      }
      
      for (TypeDef type : node.getValues()) {
        StHeaderSpec spec = visitHeader(type, ctx.next(type));
        spec.getValue().accept(immutable);
        spec.getNested().accept(api);
      }
      immutable.build();
    };
        
    final TypeName typeName = node.getContext() == ContextTypeDef.ACCEPTS ?
        unit.getAccepts(node).getName() : 
        unit.getReturns(node).getName();
    return ImmutableStHeaderSpec.builder()
        .value((immutable) -> immutable.method(node, typeName).build())
        .nested(nested).build();
  }

  @Override
  public StSpec visitClassName(InvocationNode invocation, HdesTree ctx) {
    return ImmutableStHeaderSpec.builder()
        .value((immutable) -> {})
        .nested((api) -> {}).build();
  }

  @Override
  public StSpec visitMapping(ObjectMappingDef mapping, HdesTree ctx) {
    return ImmutableStHeaderSpec.builder()
        .value((immutable) -> {})
        .nested((api) -> {}).build();
  }

  @Override
  public StSpec visitCommandInvocation(CommandInvocation command, HdesTree ctx) {
    return ImmutableStHeaderSpec.builder()
        .value((immutable) -> {})
        .nested((api) -> {}).build();
  }

  @Override
  public StSpec visitPromise(ServicePromise promise, HdesTree ctx) {
    // TODO Auto-generated method stub
    return null;
  }
}
