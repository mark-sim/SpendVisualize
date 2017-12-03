package com.mark.app;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDBHandler {

    private static Map<String, Double> typeAmount = new HashMap<String,Double>();
    private static final String CONNECTION_URL = "http://localhost:8086";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "root";

    public static void insertIntoInfluxDB(List<Metadata> metadatas) {
        InfluxDB influxDB = InfluxDBFactory.connect(CONNECTION_URL, DEFAULT_USER, DEFAULT_PASS);
        String dbName = "Transaction";
        if(influxDB.databaseExists(dbName)) {
            influxDB.deleteDatabase(dbName);
        }

        influxDB.createDatabase(dbName);

        BatchPoints batchPoints = BatchPoints
                                        .database(dbName)
                                        .tag("async", "true")
                                        .consistency(InfluxDB.ConsistencyLevel.ALL)
                                        .build();

        for(Metadata m : metadatas) {
            String name = m.getName();
            String type = m.getType();
            Double amount = m.getAmount();
            Date date = m.getDate().getTime();

            Point point = Point.measurement("transaction")
                                                .time(date.getTime(), TimeUnit.MILLISECONDS)
                                                .addField("Name", name)
                                                .addField("Type", type)
                                                .addField("Amount", amount)
                                                .build();
            batchPoints.point(point);
            calculateTypeAmount(type, amount);
        }

        // i is used here to increment time for each point since time has to be unique.
        long i = 0;
        for(String type : typeAmount.keySet()) {
            Double amount = typeAmount.get(type);
            Point point = Point.measurement("typeAmount")
                                                .time(System.currentTimeMillis() + i, TimeUnit.MILLISECONDS)
                                                .addField("Type", type)
                                                .addField("Amount", amount)
                                                .build();
            batchPoints.point(point);
            i += 10000000;
        }
        influxDB.write(batchPoints);
    }

    private static void calculateTypeAmount(String type, Double amount) {
        Double value = 0.0;
        if(typeAmount.get(type) != null) {
            value = typeAmount.get(type);
        }
        typeAmount.put(type, amount + value);
    }
}
