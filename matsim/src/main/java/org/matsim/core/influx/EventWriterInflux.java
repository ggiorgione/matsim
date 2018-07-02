package org.matsim.core.influx;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.influxdb.dto.Point;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.algorithms.EventWriter;
import org.matsim.core.events.handler.BasicEventHandler;

import java.util.concurrent.TimeUnit;

@Singleton
public class EventWriterInflux implements EventWriter, BasicEventHandler {

    @Inject
    private InfluxManager influxManager;

    @Override
    public void closeFile() {
        influxManager.close();
    }

    private int currentIteration;

    /**
     * Used to enforce that every event has a unique timestamp. Works as long as there are less than a billion events
     * within one time unit of the simulation.
     */
    private int nanos = 0;

    @Override
    public void handleEvent(Event event) {
        Point.Builder p = event.toPoint(currentIteration);
        p.time((long) (event.getTime() * 1000000000) + nanos, TimeUnit.NANOSECONDS);
        nanos = (nanos + 1) % 1000000000;
        influxManager.write(p.build());
    }

    @Override
    public void reset(int nextIteration) {
        currentIteration = nextIteration;
    }

}
