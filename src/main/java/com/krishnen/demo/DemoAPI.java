/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.krishnen.demo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * A simple REST service which is able to receive an incoming message and call a service which will process it
 * The idea here is to be able to receive the message and exit as quickly as possible
 *
 * @author krishnenc@gmail.com
 *
 */

@Path("/")
public class DemoAPI {
    @Inject
    DemoService demoService;

    @GET
    @Path("/json/{incoming}")
    @Produces({ "application/json" })
    public String getHelloWorldJSON(@PathParam("incoming") String incoming) {
        return "{\"result\":\"" + demoService.processJobAsync(incoming) + "\"}";
    }
}
