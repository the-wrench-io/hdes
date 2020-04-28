package io.resys.hdes.aproc;

/*-
 * #%L
 * hdes-maven-plugin
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.sonatype.plexus.build.incremental.BuildContext;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Code;
import io.resys.hdes.compiler.api.HdesCompiler.CodeValue;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;

@Mojo(name = "hdes", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class HdesMojo extends AbstractMojo {
  @Parameter(property = "project.build.sourceEncoding")
  protected String sourceEncoding;
  @Parameter(property = "project", required = true, readonly = true)
  protected MavenProject project;
  @Parameter(defaultValue = "${basedir}/src/main/hdes")
  private File sourceDirectory;
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/hdes")
  private File outputDirectory;
  @Parameter(defaultValue = "${project.build.directory}/maven-status/hdes", readonly = true)
  private File statusDirectory;
  @Component
  private BuildContext buildContext;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Log log = getLog();
    String sourceEncoding = getEncoding(this.sourceEncoding);
    log.info("HDES source: " + sourceDirectory);
    log.info("HDES output: " + outputDirectory);
    if (!sourceDirectory.isDirectory()) {
      log.info("No HDES sources to compile in: " + sourceDirectory.getAbsolutePath());
      return;
    }
    HdesCompiler.Parser parser = JavaHdesCompiler.config().build().parser();
    for (File file : getHdesFiles(sourceDirectory)) {
      getLog().info("Compiling HDES source: " + file.getAbsolutePath());
      parser.add(file.getAbsolutePath(), getSource(file, sourceEncoding));
    }
    Code code = parser.build();
    File outputDirectory = getOutputDirectory(this.outputDirectory);
    for (CodeValue value : code.getValues()) {
      createFile(value, outputDirectory, sourceEncoding);
    }
  }

  private void createFile(CodeValue value, File outputDirectory, String sourceEncoding) throws MojoExecutionException {
    FileOutputStream file = null;
    try {
      File packageDirectory = new File(outputDirectory, value.getPackageName().replaceAll(".", File.separator));
      if(!packageDirectory.exists()) {
        packageDirectory.mkdirs();
      } 
      file = new FileOutputStream(new File(packageDirectory, value.getSimpleName()));
      
      IOUtils.write(value.getTarget().getBytes(StandardCharsets.UTF_8), file);
    } catch (Exception e) {

      getLog().error(e);
      throw new MojoExecutionException("Failed to write file: " + value.getPackageName() + "." + value.getSimpleName() + " because of: " + e.getMessage(), e);
    } finally {
      try {
        if(file != null) {
          file.close();
        }
      } catch(IOException ioe) { 
        getLog().error(ioe);
      }
    }
  }

  private String getSource(File file, String encoding) throws MojoExecutionException {
    try {
      return IOUtils.toString(new FileInputStream(file), encoding);
    } catch (Exception e) {
      getLog().error(e);
      throw new MojoExecutionException("Failed to read file: " + file.getAbsolutePath() + " because of: " + e.getMessage(), e);
    }
  }

  private Set<File> getHdesFiles(File sourceDirectory) throws MojoExecutionException {
    try {
      Set<String> includes = Collections.singleton("**/*.hdes");
      Set<String> excludes = Collections.emptySet();
      SourceInclusionScanner scan = new SimpleSourceInclusionScanner(includes, excludes);
      scan.addSourceMapping(new SuffixMapping("hdes", Collections.emptySet()));
      return scan.getIncludedSources(sourceDirectory, null);
    } catch (Exception e) {
      getLog().error(e);
      throw new MojoExecutionException("Failed to scan for source files because of: " + e.getMessage(), e);
    }
  }

  private File getOutputDirectory(File outputDirectory) {
    if (!outputDirectory.exists()) {
      outputDirectory.mkdirs();
    }
    return outputDirectory;
  }

  private String getEncoding(String encoding) {
    if (encoding == null) {
      return Charset.defaultCharset().name();
    }
    return Charset.forName(encoding.trim()).name();
  }
}
