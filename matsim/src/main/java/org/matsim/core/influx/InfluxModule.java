package org.matsim.core.influx;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.listener.ShutdownListener;

import javax.inject.Inject;

import static org.matsim.core.config.groups.ControlerConfigGroup.EventsFileFormat.influx;

public class InfluxModule extends AbstractModule {

    @Inject
    private ControlerConfigGroup controlerConfigGroup;

    private String url;

    public InfluxModule(String url) {
        this.url = url;
    }

    @Provides @Singleton
    private InfluxManager influxManager() {
        return new InfluxManager(url);
    }

    @Override
    public void install() {
        bind(EventWriterInflux.class);
        if (controlerConfigGroup.getEventsFileFormats().contains(influx)) {
            addEventHandlerBinding().to(EventWriterInflux.class);
        }
        addControlerListenerBinding().to(InfluxShutdownListener.class);
    }

    public static class InfluxShutdownListener implements ShutdownListener {

        @Inject
        private InfluxManager influxManager;

        @Override
        public void notifyShutdown(ShutdownEvent event) {
            influxManager.close();
        }
    }

}
