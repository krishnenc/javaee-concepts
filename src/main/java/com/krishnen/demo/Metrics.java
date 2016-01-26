package com.krishnen.demo;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by User on 1/26/2016.
 * This class instantiates the metrics library for general use and configures the library
 * to write all the stats to Graphite
 */
@Singleton
@Startup
public class Metrics {

    private final MetricRegistry metrics = new MetricRegistry();

    public MetricRegistry getMetrics() {
        return this.metrics;
    }

    @PostConstruct
    public void createMetricsRegistry() {
        final Graphite graphite = new Graphite(new InetSocketAddress("127.0.0.1", 2003));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith("jobs.server")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        reporter.start(1, TimeUnit.MINUTES);
    }
}
