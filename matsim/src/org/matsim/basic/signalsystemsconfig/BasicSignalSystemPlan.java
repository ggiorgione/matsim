/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package org.matsim.basic.signalsystemsconfig;

import java.util.Map;

import org.matsim.api.basic.v01.Id;
/**
 * 
 * @author dgrether
 *
 */
public interface BasicSignalSystemPlan {

	public void setStartTime(double seconds);

	public void setEndTime(double seconds);

	public Id getId();

	public void addLightSignalGroupConfiguration(
			BasicSignalGroupSettings groupConfig);

	public double getStartTime();

	public double getEndTime();

	public Map<Id, BasicSignalGroupSettings> getGroupConfigs();

	public void setCirculationTime(Integer circulationTimeSec);

	public void setSyncronizationOffset(Integer seconds);

	public Integer getSyncronizationOffset();

	public Integer getCirculationTime();

}