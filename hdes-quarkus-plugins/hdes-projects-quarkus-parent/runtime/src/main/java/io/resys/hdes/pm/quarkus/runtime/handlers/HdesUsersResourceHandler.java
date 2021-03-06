package io.resys.hdes.pm.quarkus.runtime.handlers;
/*-
 * #%L
 * hdes-ui-quarkus
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

import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.api.ImmutableBatchUser;
import io.resys.hdes.projects.api.ImmutableUser;
import io.resys.hdes.projects.api.PmRepository.BatchUser;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserResource;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesUsersResourceHandler extends HdesResourceHandler {

  public HdesUsersResourceHandler(
      CurrentIdentityAssociation currentIdentityAssociation,
      CurrentVertxRequest currentVertxRequest) {
    super(currentIdentityAssociation, currentVertxRequest);
  }
  
  @Override
  protected void handleResource(RoutingContext event, HttpServerResponse response, HdesProjectsContext ctx) {
    switch (event.request().method()) {
    case GET:
      Collection<UserResource> defs = ctx.repo().query().users().find();
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(defs)));
      break;

    case DELETE:
      User toDelete = ctx.reader().build(event.getBody().getBytes(), ImmutableUser.class);
      User deleted = ctx.repo().delete().user(toDelete.getId(), toDelete.getRev());
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(deleted)));
      break;

    case POST:
      BatchUser create = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchUser.class);
      UserResource created = ctx.repo().create().user(create);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(created)));
      break;

    case PUT:
      BatchUser update = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchUser.class);
      UserResource updated = ctx.repo().update().user(update);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(updated)));
      break;
    default:
      break;
    }
  }
}
