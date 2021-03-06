package io.resys.hdes.backend.spi.storage.classpath;

/*-
 * #%L
 * hdes-ui-backend
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.backend.api.HdesBackend.ClasspathStorageConfig;
import io.resys.hdes.backend.api.HdesBackend.ConfigType;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.StorageConfig;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableClasspathStorageConfig;

public class ClasspathHdesBackendStorage implements HdesBackendStorage {
  
  private Map<String, Def> cache = new HashMap<>();

  private final ClasspathStorageConfig storageConfig;
  
  public ClasspathHdesBackendStorage(ClasspathStorageConfig storageConfig) {
    super();
    this.storageConfig = storageConfig;
  }

  @Override
  public StorageReader read() {
    return new StorageReader() {
      @Override
      public Collection<Def> build() {
        return cache.values();
      }
    };
  }

  @Override
  public ErrorReader errors() {
    return new ErrorReader() {
      @Override
      public Collection<DefError> build() {
        return Collections.emptyList();
      }
    };
  }
  
  @Override
  public StorageWriter write() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  public static Config create() {
    return new Config();
  }
  
  public static class Config {
    public ClasspathHdesBackendStorage build() {
      return new ClasspathHdesBackendStorage(ImmutableClasspathStorageConfig.builder().type(ConfigType.CLASSPATH).build());
    }
  }

  @Override
  public StorageConfig config() {
    return storageConfig;
  }
}
