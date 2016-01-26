package com.krishnen.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;

/**
 * Created by User on 26/01/2016.
 *
 * This bean initializes the redis connection pool  and clears the DB on startup
 */
@javax.ejb.Singleton
@Startup
public class Redis {

    private JedisPool pool;

    @PostConstruct
    public void createConnectionPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5000);
        poolConfig.setMaxIdle(1000);
        poolConfig.setMaxWaitMillis(5000);
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        clearRedis();
    }

    @PreDestroy
    public void destroyConnectionPool(){
        if (pool!= null)
        {
            pool.destroy();
        }

    }

    public JedisPool getRedisConnectionPool() {
        return pool;
    }

    private void clearRedis(){
        Jedis jedis = pool.getResource();
        try {
            jedis.flushDB();
        } catch (JedisConnectionException e) {
            // returnBrokenResource when the state of the object is unrecoverable
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            /// ... it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }
    }
}
