/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package playground.johannes.gsv.zones;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.algorithm.locate.IndexedPointInAreaLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * @author johannes
 * 
 */
public class ZoneCollection {

	private final Set<Zone> zones;

	private SpatialIndex spatialIndex;

	private Map<String, Zone> keyIndex;

	private String primaryKey;

	public ZoneCollection() {
		zones = new HashSet<>();
	}

	public void setPrimaryKey(String key) {
		this.primaryKey = key;
		buildKeyIndex();
	}

	public void addAll(Collection<Zone> zones) {
		this.zones.addAll(zones);
		buildIndex();
	}

	private void buildIndex() {
		buildKeyIndex();
		buildSpatialIndex();
	}

	private void buildKeyIndex() {
		keyIndex = new HashMap<>();
		if (primaryKey != null) {
			for (Zone zone : zones) {
				keyIndex.put(zone.getAttribute(primaryKey), zone);
			}
		}
	}

	private void buildSpatialIndex() {
		//TODO check SRID fields
		//TODO check for overlapping polygons
		
		STRtree tree = new STRtree();
		for (Zone zone : zones) {
			tree.insert(zone.getGeometry().getEnvelopeInternal(), new IndexEntry(zone));
		}

		tree.build();

		spatialIndex = tree;

	}

	public Zone get(String key) {
		return keyIndex.get(key);
	}

	public Zone get(Coordinate c) {
		List<IndexEntry> candidates = spatialIndex.query(new Envelope(c));

		if (candidates.size() == 1) {
			return candidates.get(0).zone;
		}

		for (IndexEntry entry : candidates) {
			if (entry.contains(c)) {
				return entry.zone;
			}
		}

		return null;
	}

	private class IndexEntry {

		private IndexedPointInAreaLocator locator;

		private final Zone zone;

		private IndexEntry(Zone zone) {
			this.zone = zone;
		}

		private boolean contains(Coordinate c) {
			if (locator == null) {
				locator = new IndexedPointInAreaLocator(zone.getGeometry());
			}

			if (locator.locate(c) == Location.INTERIOR)
				return true;
			else
				return false;
		}
	}
}