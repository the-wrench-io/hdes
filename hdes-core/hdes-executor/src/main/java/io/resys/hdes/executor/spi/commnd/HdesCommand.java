package io.resys.hdes.executor.spi.commnd;

/*-
 * #%L
 * hdes-executor
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

import io.resys.hdes.executor.spi.commnd.HdesCommand.Mapping;
import io.resys.hdes.executor.spi.commnd.HdesCommand.Output;

public interface HdesCommand<T extends Mapping, O extends Output> {
  
  interface Mapping extends Serializable {}
  interface Output extends Serializable {}
  interface DynamicOutput extends Output {}

  O accept(Serializable accepts, T mapping);
}
