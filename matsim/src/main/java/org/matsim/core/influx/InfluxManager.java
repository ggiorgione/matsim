package org.matsim.core.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InfluxManager implements Closeable {

    private String dbName;

    private InfluxDB db;

    public InfluxManager(String url, boolean enableConnection) {
        if(enableConnection){
            dbName = "matsim_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy_MM_dd_hh_mm"));
            db = InfluxDBFactory.connect(url);
            db.query(new Query("CREATE DATABASE " + dbName, dbName));
            db.setDatabase(dbName);
            db.enableBatch();
        }

    }

    @Override
    public void close() {
        if(db!= null) db.close();
    }

    public void write(Point p) {
        if(db!=null) db.write(p);
    }

}
