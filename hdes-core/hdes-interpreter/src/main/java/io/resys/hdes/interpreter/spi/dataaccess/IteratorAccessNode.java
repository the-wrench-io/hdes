package io.resys.hdes.interpreter.spi.dataaccess;

/*-
 * #%L
 * hdes-interpreter
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

import java.io.Serializable;

import io.resys.hdes.ast.api.nodes.ImmutablePosition;
import io.resys.hdes.ast.api.nodes.ImmutableToken;
import io.resys.hdes.ast.spi.util.Assertions;
import io.resys.hdes.interpreter.api.HdesInterpreter.DataAccessNode;

public class IteratorAccessNode implements DataAccessNode {

  private final Serializable entries;
  private final Token token = ImmutableToken.builder().text("")
      .start(ImmutablePosition.builder().line(-1).col(-1).build())
      .build();
  
  private IteratorAccessNode(Serializable entries) {
    super();
    this.entries = entries;
  }

  @Override
  public Serializable get(String name) {
    return entries;
  }
  
  public Serializable getValue() {
    return entries;
  }
  
  public static IteratorAccessNode create(Serializable entries) {
    Assertions.notNull(entries, () -> "entries can't be null!");
    return new IteratorAccessNode(entries);
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public IteratorAccessNode build(Serializable entries) {
      return new IteratorAccessNode(entries);
    }
  }

  @Override
  public Token getToken() {
    return token;
  }
}
