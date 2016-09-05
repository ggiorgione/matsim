/* *********************************************************************** *
 * project: org.matsim.*
 * DgController
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package tutorial.fixedTimeSignals;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.signals.SignalSystemsConfigGroup;
import org.matsim.contrib.signals.controler.SignalsModule;
import org.matsim.contrib.signals.data.SignalsData;
import org.matsim.contrib.signals.data.SignalsDataLoader;
import org.matsim.contrib.signals.otfvis.OTFVisWithSignalsLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;


/**
 * Configures and runs MATSim with traffic signals from input files
 * and visualizes it with OTFVis. 
 * 
 * @author dgrether
 */
public class RunSignalSystemsExample {

	/**
	 * @param args is ignored
	 */
	public static void main(String[] args) {
		run(true);
	}

	static void run(boolean usingOTFVis) {
		String inputDir = "./../../matsim/examples/equil-extended/";
		
		Config config = ConfigUtils.loadConfig(inputDir + "config.xml") ;
		
		config.controler().setLastIteration(0); // use higher values if you want to iterate
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists);
		
		config.network().setInputFile("network.xml");
		config.plans().setInputFile("plans2000.xml.gz");
		
		config.qsim().setTrafficDynamics(TrafficDynamics.withHoles);
		config.qsim().setSnapshotStyle(SnapshotStyle.withHoles);
		config.qsim().setNodeOffset(5.);
		
		SignalSystemsConfigGroup signalConfig = ConfigUtils.addOrGetModule(config, SignalSystemsConfigGroup.GROUPNAME, SignalSystemsConfigGroup.class ) ;
		
		// the following makes the contrib load the signalSystems files, but not to do anything with them:
		// (this switch will eventually go away)
		signalConfig.setUseSignalSystems(true);

		// these are the paths to the signal systems definition files:
		signalConfig.setSignalSystemFile(inputDir + "signalSystems_v2.0.xml");
		signalConfig.setSignalGroupsFile(inputDir + "signalGroups_v2.0.xml");
		signalConfig.setSignalControlFile(inputDir + "signalControl_v2.0.xml");
		
//		config.travelTimeCalculator().setCalculateLinkToLinkTravelTimes(true);
//		config.controler().setLinkToLinkRoutingEnabled(true);
		
		if (usingOTFVis) {
			OTFVisConfigGroup otfvisConfig = ConfigUtils.addOrGetModule(config, OTFVisConfigGroup.GROUP_NAME, OTFVisConfigGroup.class);
			otfvisConfig.setScaleQuadTreeRect(true); // make links visible beyond screen edge
			otfvisConfig.setColoringScheme(ColoringScheme.byId);
			otfvisConfig.setAgentSize(240);
		}

		// --- create the scenario
		Scenario scenario = ScenarioUtils.loadScenario( config ) ;
		scenario.addScenarioElement(SignalsData.ELEMENT_NAME, new SignalsDataLoader(signalConfig).loadSignalsData());
		
		// --- create the controler
		Controler c = new Controler( scenario );

		// add the signals module to the simulation
		c.addOverridingModule(new SignalsModule());
		// add the visualization module to the simulation
		if ( usingOTFVis ) {
			c.addOverridingModule( new OTFVisWithSignalsLiveModule() );
		}
		
		//do it, do it, do it, now
		c.run();
	}
}
