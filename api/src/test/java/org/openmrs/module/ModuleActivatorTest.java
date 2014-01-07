/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests methods of the module activator that do not require refreshing of the spring application
 * context. For those that require refreshing, see WebModuleActivatorTest
 */
public class ModuleActivatorTest extends BaseModuleActivatorTest {
	
	@Test
	public void shouldCallWillStartOnStartup() throws Exception {
		assertTrue(moduleTestData.getWillStartCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getWillStartCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getWillStartCallCount(MODULE3_ID) == 1);
	}
	
	@Test
	public void shouldNotCallStartedOnStartup() throws Exception {
		assertTrue(moduleTestData.getStartedCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getStartedCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getStartedCallCount(MODULE3_ID) == 0);
	}
	
	@Test
	public void shouldNotCallWillStopOnStartup() throws Exception {
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 0);
	}
	
	@Test
	public void shouldNotCallStoppedOnStartup() throws Exception {
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 0);
	}
	
	@Test
	public void shouldStartModulesInOrder() throws Exception {
		//module2 depends on module1 while module3 depends on module2
		//so startup order should be module1, module2, module3
		assertTrue(moduleTestData.getWillStartCallTime(MODULE1_ID) <= moduleTestData.getWillStartCallTime(MODULE2_ID));
		assertTrue(moduleTestData.getWillStartCallTime(MODULE2_ID) <= moduleTestData.getWillStartCallTime(MODULE3_ID));
		
		assertTrue(moduleTestData.getStartedCallTime(MODULE1_ID) <= moduleTestData.getStartedCallTime(MODULE2_ID));
		assertTrue(moduleTestData.getStartedCallTime(MODULE2_ID) <= moduleTestData.getStartedCallTime(MODULE3_ID));
	}
	
	@Test
	public void shouldCallWillStopAndStoppedOnlyForStoppedModule() throws Exception {
		ModuleFactory.stopModule(ModuleFactory.getModuleById(MODULE3_ID));
		
		//should have called willStop() for only module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 0);
		
		//should have called stopped() for only module3
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 0);
	}
	
	@Test
	public void shouldStopDependantModulesOnStopModule() throws Exception {
		//since module2 depends on module1, and module3 depends on module2
		//stopping module1 should also stop both module2 and module3
		ModuleFactory.stopModule(ModuleFactory.getModuleById(MODULE1_ID));
		
		//should have called willStop() for all module1, module2 and module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		
		//should have called stopped() for all module1, module2 and module3
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
		
		//willStop() and stopped() should have been called in the right order
		//which is the reverse of the startup. that is module3, module2, module1
		assertTrue(moduleTestData.getWillStopCallTime(MODULE3_ID) <= moduleTestData.getWillStopCallTime(MODULE2_ID));
		assertTrue(moduleTestData.getWillStopCallTime(MODULE2_ID) <= moduleTestData.getWillStopCallTime(MODULE1_ID));
		
		assertTrue(moduleTestData.getStoppedCallTime(MODULE3_ID) <= moduleTestData.getStoppedCallTime(MODULE2_ID));
		assertTrue(moduleTestData.getStoppedCallTime(MODULE2_ID) <= moduleTestData.getStoppedCallTime(MODULE1_ID));
	}
	
	@Test
	public void shouldCallWillStopAndStoppedOnShutdown() throws Exception {
		ModuleUtil.shutdown();
		
		//should have called willStop() for module1, module2, and module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		
		//should have called stopped() for module1, module2, and module3
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
		
		//willStop() should have been called before stopped() for module1, module2, and module3
		assertTrue(moduleTestData.getWillStopCallTime(MODULE1_ID) <= moduleTestData.getStoppedCallTime(MODULE1_ID));
		assertTrue(moduleTestData.getWillStopCallTime(MODULE2_ID) <= moduleTestData.getStoppedCallTime(MODULE2_ID));
		assertTrue(moduleTestData.getWillStopCallTime(MODULE3_ID) <= moduleTestData.getStoppedCallTime(MODULE3_ID));
	}
	
	@Test
	public void shouldExcludePrevouslyStoppedModulesOnShutdown() {
		//At OpenMRS shutdown, willStop() and stopped() methods get called for all 
		//started module's activator EXCLUDING any module(s) that were previously stopped.
		
		//now let us make module3 be the previously stopped module
		ModuleFactory.stopModule(ModuleFactory.getModuleById(MODULE3_ID));
		
		//should have called willStop() and stopped() for only module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
		
		//now shutdown
		ModuleUtil.shutdown();
		
		//should have called willStop() and stopped() for module1 and module2
		//while willStop() and stopped() should not be called again for module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
	}
	
	@Test
	public void shouldCallWillStopAndStoppedOnUnloadModule() throws Exception {
		
		ModuleFactory.unloadModule(ModuleFactory.getModuleById(MODULE3_ID));
		
		//should have called willStop() and stopped() for module3
		assertTrue(moduleTestData.getWillStopCallCount(MODULE3_ID) == 1);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE3_ID) == 1);
		
		//should not call willStop() and stopped() for module1 and module2
		assertTrue(moduleTestData.getWillStopCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getWillStopCallCount(MODULE2_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE1_ID) == 0);
		assertTrue(moduleTestData.getStoppedCallCount(MODULE2_ID) == 0);
	}
}
