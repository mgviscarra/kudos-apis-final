package com.mgvr.kudos.api.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfluxDb {
    @Value("${management.metrics.export.influx.db}")
    private String influxDb;

    @Value("${management.metrics.export.influx.uri}")
    private String influxUri;

    private static InfluxDB instance;

    public  InfluxDB getInstance(){
        if(instance==null){
            instance = InfluxDBFactory.connect(influxUri);
            instance.setDatabase(influxDb);
        }
        return instance;
    }
}
