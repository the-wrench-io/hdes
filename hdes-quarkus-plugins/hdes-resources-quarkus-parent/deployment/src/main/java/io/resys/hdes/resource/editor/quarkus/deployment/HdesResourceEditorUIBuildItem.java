package io.resys.hdes.resource.editor.quarkus.deployment;

/*-
 * #%L
 * hdes-projects-quarkus-deployment
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.quarkus.builder.item.SimpleBuildItem;

public final class HdesResourceEditorUIBuildItem extends SimpleBuildItem {
  private final String projectsUiFinalDestination;
  private final String projectsUiPath;

  public HdesResourceEditorUIBuildItem(String projectsUiFinalDestination, String projectsUiPath) {
    super();
    this.projectsUiFinalDestination = projectsUiFinalDestination;
    this.projectsUiPath = projectsUiPath;
  }

  public String getUiFinalDestination() {
      return projectsUiFinalDestination;
  }

  public String getUiPath() {
      return projectsUiPath;
  }

}
