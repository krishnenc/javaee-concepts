package com.krishnen.demo;

import com.codahale.metrics.Meter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * Created by User on 26/01/2016.
 */
public class JobTask implements Callable<String> {

    @Inject
    Metrics metrics;

    @Inject
    Redis redis;

    @Override
    public String call() {
        Meter job_process_rate = metrics.getMetrics().meter(Constants.JOBS_PROCESS_RATE);
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = redis.getRedisConnectionPool();
            jedis = pool.getResource();
            List<String> payload = jedis.brpop(0, Constants.JOBS_QUEUE);
            //TODO perform some processing on the payload here
            //Mark processing of the job here in the metrics library
            //These metrics are aggregated and sent to graphite
            job_process_rate.mark();
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
        return "Done processing Callable Task ";
    }
}
