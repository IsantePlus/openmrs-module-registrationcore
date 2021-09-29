package org.openmrs.module.registrationcore.api;

import org.junit.Ignore;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations={"classpath*:applicationContext-service.xml","classpath*:moduleApplicationContext.xml", "classpath:registrationcore-test-applicationContext.xml"}, inheritLocations=false)
@Ignore("Skipping failed tests. See https://github.com/IsantePlus/openmrs-module-registrationcore/issues/44")
public class RegistrationCoreSensitiveTestBase extends BaseModuleContextSensitiveTest {
	
}
