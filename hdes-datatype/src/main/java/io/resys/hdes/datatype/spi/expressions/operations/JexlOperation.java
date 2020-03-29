package io.resys.hdes.datatype.spi.expressions.operations;

/*-
 * #%L
 * hdes-datatype
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

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;

import io.resys.hdes.datatype.api.DataTypeService.Operation;

public class JexlOperation implements Operation {
 
  private final static JexlEngine jexl = new JexlBuilder().create();
  
  private final JexlExpression expression;
 
  public JexlOperation(String expression) {
    super();
    this.expression = jexl.createExpression( expression );
  }
  
  @Override
  public Object apply(Object entity) {
    return expression.evaluate((JexlContext) entity);
  }
}
