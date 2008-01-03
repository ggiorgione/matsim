/* *********************************************************************** *
 * project: org.matsim.*
 * Leg.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package org.matsim.plans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.matsim.basic.v01.BasicLeg;
import org.matsim.basic.v01.BasicNode;
import org.matsim.basic.v01.BasicRoute;
import org.matsim.utils.misc.Time;

public class Leg extends BasicLeg implements Serializable{

	//////////////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = 5123937717277263980L;

	private double depTime = Time.UNDEFINED_TIME;
	private double travTime = Time.UNDEFINED_TIME;
	private double arrTime = Time.UNDEFINED_TIME;

	public Leg(final String num, final String mode, final String depTime, final String travTime, final String arrTime) {
		if (num != null) {
			this.num = Integer.parseInt(num);
			if (this.num < 0) {
				throw new NumberFormatException("A Leg's num has to be an  integer >= 0.");
			}
		}
		this.mode = mode.intern();
		if (depTime != null) {
			this.depTime = Time.parseTime(depTime);
		}
		if (travTime != null) {
			this.travTime = Time.parseTime(travTime);
		}
		if (arrTime != null) {
			this.arrTime = Time.parseTime(arrTime);
		}
	}

	public Leg(final int num, final String mode, final double depTime, final double travTime, final double arrTime) {
		this.num = num;
		if ((this.num < 0) && (this.num != Integer.MIN_VALUE)) {
			throw new NumberFormatException("A Leg's num has to be an  integer >= 0.");
		}
		this.mode = mode.intern();
		this.depTime = depTime;
		this.travTime = travTime;
		this.arrTime = arrTime;
	}
	
	/**
	 * Makes a deep copy of this leg, however only when the Leg has a route which is 
	 * instance of Route or BasicRoute. Other route instances are not considered.
	 * @param leg
	 */
	public Leg(final Leg leg) {
		this.num = leg.num;
		this.mode = leg.mode;
		this.depTime = leg.depTime;
		this.arrTime = leg.arrTime;
		this.travTime = leg.travTime;
		if (leg.route instanceof Route) {
			this.route = new Route((Route) leg.route);	
		}
		else {
			this.route = new BasicRoute<BasicNode>();
			this.route.setRoute(leg.getRoute().getRoute());
		}
		
	}

	//////////////////////////////////////////////////////////////////////
	// create methods
	//////////////////////////////////////////////////////////////////////

	public final Route createRoute(final String dist, final String time) {
		this.route = new Route(dist, time);
		return getRoute();
	}

	//////////////////////////////////////////////////////////////////////
	// remove methods
	//////////////////////////////////////////////////////////////////////

	protected final void removeRoute() {
		this.route = null;
	}

	//////////////////////////////////////////////////////////////////////
	// get methods
	//////////////////////////////////////////////////////////////////////

	
	public final double getDepTime() {
		return this.depTime;
	}

	public final double getTravTime() {
		return this.travTime;
	}

	public final double getArrTime() {
		return this.arrTime;
	}

	@Override
	public Route getRoute() {
		return (Route) this.route;
	}

	//////////////////////////////////////////////////////////////////////
	// set methods
	//////////////////////////////////////////////////////////////////////

	public final void setDepTime(final double depTime) {
		this.depTime = depTime;
	}

	public final void setTravTime(final double travTime) {
		this.travTime = travTime;
	}

	public final void setArrTime(final double arrTime) {
		this.arrTime = arrTime;
	}

	@Override
	public final String toString() {
		return "[num=" + this.num + "]" +
				"[mode=" + this.mode + "]" +
				"[depTime=" + Time.writeTime(this.depTime) + "]" +
				"[travTime=" + Time.writeTime(this.travTime) + "]" +
				"[arrTime=" + Time.writeTime(this.arrTime) + "]" +
				"[route=" + this.route + "]";
	}



	// BasicLeg is not yet serializable, so we have to serialize it by hand
	private void writeObject(final ObjectOutputStream s) throws IOException
	{
	    // The standard non-transient fields.
	  s.defaultWriteObject();
	  s.writeInt(getNum());
	  s.writeObject(getMode());
	  s.writeObject(getRoute());
	}

	private void readObject(final ObjectInputStream s)
	  throws IOException, ClassNotFoundException
	{
	  // the `size' field.
	  s.defaultReadObject();
	  setNum(s.readInt());
	  setMode((String)s.readObject());
	  setRoute((Route)s.readObject());
	}

}
