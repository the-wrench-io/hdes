//package io.resys.hdes.projects.quarkus.deployment.test;
//
///*-
// * #%L
// * hdes-ui-quarkus-deployment
// * %%
// * Copyright (C) 2020 Copyright 2020 ReSys OÜ
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.StringAsset;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//import io.quarkus.test.QuarkusUnitTest;
//import io.restassured.RestAssured;
//
//
////-Djava.util.logging.manager=org.jboss.logmanager.LogManager
//public class DisabledTest {
//  @RegisterExtension
//  final static QuarkusUnitTest config = new QuarkusUnitTest()
//      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
//          .addAsResource(new StringAsset("quarkus.hdes-projects.connection-url=localhost:12345"), "application.properties")
//          //.addAsResource(new StringAsset("quarkus.hdes-projects.enable=false"), "application.properties")
//          //.addAsResource(new StringAsset("quarkus.profile=test"), "application.properties")
//          //.addAsResource(new StringAsset("quarkus.http.port=8090"), "application.properties")
//          );
//
//  @Test
//  public void shouldUseDefaultConfig() {
//    RestAssured.when().get("/hdes-projects").then().statusCode(404);
//    RestAssured.when().get("/hdes-projects/index.html").then().statusCode(404);
//  }
//}
