package io.resys.hdes.ast.spi.visitors.ast;

import io.resys.hdes.ast.HdesParser.HdesBodyContext;
import io.resys.hdes.ast.HdesParserBaseVisitor;

/*-
 * #%L
 * hdes-ast
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

import io.resys.hdes.ast.api.nodes.AstNode;


public class HdesParserAstNodeVisitor extends HdesParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitHdesBody(HdesBodyContext ctx) {
    // TODO Auto-generated method stub
    return super.visitHdesBody(ctx);
  }
}
