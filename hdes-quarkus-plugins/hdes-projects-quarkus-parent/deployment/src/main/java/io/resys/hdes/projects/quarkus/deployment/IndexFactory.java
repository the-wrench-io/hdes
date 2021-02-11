package io.resys.hdes.projects.quarkus.deployment;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import io.quarkus.deployment.configuration.ConfigurationError;
import io.quarkus.deployment.util.FileUtil;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class IndexFactory {

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private String frontendPath;
    private String backendPath;
    private String projectsPath;
    private String usersPath;
    private String groupsPath;
    private String indexFileContent;
    
    public Builder frontend(String frontendPath) {
      this.frontendPath = frontendPath;
      return this;
    }
    public Builder backend(String backendPath) {
      this.backendPath = backendPath;
      return this;
    }
    public Builder backendProjects(String projectsPath) {
      this.projectsPath = projectsPath;
      return this;
    }
    public Builder backendUsers(String usersPath) {
      this.usersPath = usersPath;
      return this;
    }
    public Builder backendGroups(String groupsPath) {
      this.groupsPath = groupsPath;
      return this;
    }
    public Builder index(Path path) {
      File file = path.toFile();
      try(InputStream stream = new FileInputStream(file)) {
        byte[] bytes = FileUtil.readFileContents(stream);
        this.indexFileContent = new String(bytes, StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new ConfigurationError(new StringBuilder("Failed to create frontend index.html, ")
            .append("msg = ").append(e.getMessage()).append(System.lineSeparator()).append(",")
            .append("path = ").append(path).append("!")
            .toString());
      }
      return this;
    }
    public Builder index(byte[] indexFileContent) {
      this.indexFileContent = new String(indexFileContent, StandardCharsets.UTF_8);
      return this;
    }
    public byte[] build() {
      RepoAssert.notEmpty(frontendPath, () -> "define frontendPath!");
      RepoAssert.notEmpty(backendPath, () -> "define backendPath!");
      RepoAssert.notEmpty(projectsPath, () -> "define projectsPath!");
      RepoAssert.notEmpty(usersPath, () -> "define usersPath!");
      RepoAssert.notEmpty(groupsPath, () -> "define groupsPath!");
      RepoAssert.notEmpty(indexFileContent, () -> "define indexFileContent!");
      
      String newPath = frontendPath.startsWith("/") ? frontendPath.substring(1) : frontendPath;
      StringBuilder newScript = new StringBuilder()
          .append(newPath)
          .append("/static/js/");
      
      StringBuilder newHref = new StringBuilder()
          .append("href=\"").append("/").append(newPath).append("/");
      
      StringBuilder newConfig = new StringBuilder()
          .append("const hdesconfig={")
          .append("ctx: \"").append(backendPath).append("\", ")
          .append("projects: \"").append(projectsPath).append("\", ")
          .append("groups: \"").append(groupsPath).append("\",")
          .append("users: \"").append(usersPath).append("\"")
          .append("}");
      
      final String original = indexFileContent;
      
      return (indexFileContent
          .replaceAll("href=\"/", newHref.toString())
          .replaceAll("static/js/", newScript.toString())
          .replaceFirst("const hdesconfig=\\{\\}", newConfig.toString())
          + "<!--" + original + "-->")
          .getBytes(StandardCharsets.UTF_8);
    }
  }
}
