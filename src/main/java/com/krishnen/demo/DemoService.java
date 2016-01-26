/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
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

import com.codahale.metrics.Meter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * A session bean that pushes the incoming message on a resis queue
 *
 * @author krishnenc@gmail.com
 *
 */
@Stateless
public class DemoService {

    @Inject
    Redis redis;

    String processJobAsync(String name) {
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = redis.getRedisConnectionPool();
            jedis = pool.getResource();
            //Push the payload on a redis list which will be popped later for processing later
            jedis.lpush(Constants.JOBS_QUEUE, name);
        }
        catch (JedisConnectionException e) {
            // returnBrokenResource when the state of the object is
            // unrecoverable
            if (null != jedis) {
                jedis.close();
            }
        }
        finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return "done";
    }

}
