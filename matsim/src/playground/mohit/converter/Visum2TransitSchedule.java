package playground.mohit.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.basic.v01.Coord;
import org.matsim.api.basic.v01.Id;
import org.matsim.api.basic.v01.TransportMode;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.IdentityTransformation;
import org.matsim.core.utils.misc.Time;
import org.matsim.transitSchedule.api.Departure;
import org.matsim.transitSchedule.api.TransitLine;
import org.matsim.transitSchedule.api.TransitRoute;
import org.matsim.transitSchedule.api.TransitRouteStop;
import org.matsim.transitSchedule.api.TransitSchedule;
import org.matsim.transitSchedule.api.TransitScheduleBuilder;
import org.matsim.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.BasicVehicle;
import org.matsim.vehicles.BasicVehicleCapacity;
import org.matsim.vehicles.BasicVehicleType;
import org.matsim.vehicles.BasicVehicles;
import org.matsim.vehicles.VehicleBuilder;

import playground.mohit.converter.VisumNetwork.VehicleCombination;
import playground.mohit.converter.VisumNetwork.VehicleUnit;


public class Visum2TransitSchedule {

	private static final Logger log = Logger.getLogger(Visum2TransitSchedule.class);

	private final VisumNetwork visum;
	private final TransitSchedule schedule;
	private final BasicVehicles vehicles;
	//	private final CoordinateTransformation coordinateTransformation = new Kilometer2MeterTransformation();
	private final CoordinateTransformation coordinateTransformation = new IdentityTransformation();
	private final Map<String, TransportMode> transportModes = new HashMap<String, TransportMode>();

	public Visum2TransitSchedule(final VisumNetwork visum, final TransitSchedule schedule, final BasicVehicles vehicles) {
		this.visum = visum;
		this.schedule = schedule;
		this.vehicles = vehicles;
	}

	public void registerTransportMode(final String visumTransportMode, final TransportMode transportMode) {
		this.transportModes.put(visumTransportMode, transportMode);
	}

	public void convert() {

		long vehId = 0;

		TransitScheduleBuilder builder = this.schedule.getBuilder();

		// 1st step: convert vehicle types
		VehicleBuilder vb = this.vehicles.getBuilder();
		for (VehicleCombination vehComb : this.visum.vehicleCombinations.values()) {
			BasicVehicleType type = vb.createVehicleType(new IdImpl(vehComb.id));
			type.setDescription(vehComb.name);
			BasicVehicleCapacity capacity = vb.createVehicleCapacity();
			VehicleUnit vu = this.visum.vehicleUnits.get(vehComb.vehUnitId);
			capacity.setSeats(Integer.valueOf(vehComb.numOfVehicles * vu.seats));
			capacity.setStandingRoom(Integer.valueOf(vehComb.numOfVehicles * (vu.passengerCapacity - vu.seats)));
			type.setCapacity(capacity);
			this.vehicles.getVehicleTypes().put(type.getId(), type);
		}

		// 2nd step: convert stop points
		final Map<Id, TransitStopFacility> stopFacilities = new TreeMap<Id, TransitStopFacility>();

		for (VisumNetwork.StopPoint stopPoint : this.visum.stopPoints.values()){
			Coord coord = this.coordinateTransformation.transform(this.visum.stops.get(this.visum.stopAreas.get(stopPoint.stopAreaId).StopId).coord);
			TransitStopFacility stop = builder.createTransitStopFacility(stopPoint.id, coord, false);
			stopFacilities.put(stopPoint.id, stop);
			this.schedule.addStopFacility(stop);
		}

		// 3rd step: convert lines
		for (VisumNetwork.TransitLine line : this.visum.lines.values()){
			TransitLine tLine = builder.createTransitLine(line.id);

			for (VisumNetwork.TimeProfile timeProfile : this.visum.timeProfiles.values()){
				VisumNetwork.VehicleCombination vehCombination = this.visum.vehicleCombinations.get(timeProfile.vehCombNr);
				if (vehCombination == null) {
					vehCombination = this.visum.vehicleCombinations.get(line.vehCombNo);
				}
				if (vehCombination == null) {
					log.error("Could not find vehicle combination with id=" + timeProfile.vehCombNr + " used in line " + line.id.toString() + ". Some TimeProfile may not be converted.");
				} else {
					BasicVehicleType vehType = this.vehicles.getVehicleTypes().get(new IdImpl(vehCombination.id));
					// convert line routes
					if (timeProfile.lineName.equals(line.id)) {
						List<TransitRouteStop> stops = new ArrayList<TransitRouteStop>();
						//  convert route profile
						for (VisumNetwork.TimeProfileItem tpi : this.visum.timeProfileItems.values()){
							if (tpi.lineName.equals(line.id.toString()) && tpi.lineRouteName.equals(timeProfile.lineRouteName.toString()) && tpi.timeProfileName.equals(timeProfile.index.toString()) && tpi.DCode.equals(timeProfile.DCode.toString())){
								TransitRouteStop s = builder.createTransitRouteStop(stopFacilities.get(this.visum.lineRouteItems.get(line.id.toString() +"/"+ timeProfile.lineRouteName.toString()+"/"+ tpi.lRIIndex.toString()+"/"+tpi.DCode).stopPointNo),Time.parseTime(tpi.arr),Time.parseTime(tpi.dep));
								stops.add(s);
							}
						}
						TransportMode mode = this.transportModes.get(line.tCode);
						if (mode == null) {
							log.error("Could not find TransportMode for " + line.tCode + ", more info: " + line.id);
						}
						TransitRoute tRoute = builder.createTransitRoute(new IdImpl(timeProfile.lineName.toString()+"."+timeProfile.lineRouteName.toString()+"."+ timeProfile.index.toString()+"."+timeProfile.DCode.toString()),null,stops,mode);
						//  convert departures
						for (VisumNetwork.Departure d : this.visum.departures.values()){
							if (d.lineName.equals(line.id.toString()) && d.lineRouteName.equals(timeProfile.lineRouteName.toString()) && d.TRI.equals(timeProfile.index.toString())) {
								Departure departure = builder.createDeparture(new IdImpl(d.index), Time.parseTime(d.dep));
								BasicVehicle veh = vb.createVehicle(new IdImpl("tr_" + vehId++), vehType);
								this.vehicles.getVehicles().put(veh.getId(), veh);
								departure.setVehicleId(veh.getId());
								tRoute.addDeparture(departure);
							}
						}
						if (tRoute.getDepartures().size() > 0) {
							tLine.addRoute(tRoute);
						} else {
							log.warn("The route " + tRoute.getId() + " was not added to the line " + tLine.getId() + " because it does not contain any departures.");
						}
					}
				}
			}
			if (tLine.getRoutes().size() > 0) {
				this.schedule.addTransitLine(tLine);
			} else {
				log.warn("The line " + tLine.getId() + " was not added to the transit schedule because it does not contain any routes.");
			}
		}
	}
}


