/* *********************************************************************** *
 * project: org.matsim.*
 * AllTests.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package org.matsim.locationchoice;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite("Test for org.matsim.locationchoice");
		//$JUnit-BEGIN$

		suite.addTestSuite(LocationChoiceTest.class);
		suite.addTestSuite(LocationMutatorwChoiceSetTest.class);
		suite.addTestSuite(RandomLocationMutatorTest.class);
		suite.addTestSuite(SubChainTest.class);
		suite.addTestSuite(ManageSubchainsTest.class);

		//$JUnit-END$
		return suite;
	}
}


