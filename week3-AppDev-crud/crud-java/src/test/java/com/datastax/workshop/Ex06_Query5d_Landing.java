package com.datastax.workshop;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Let's play !
 */ 
@RunWith(JUnitPlatform.class)
public class Ex06_Query5d_Landing extends TestFixtures implements DataModelConstants {
    
	@Test
	/**
	 * CHECK on Astra CQL Console:
	 * Must see: active=false, 'end' has valid timestamp
	 * SELECT * FROM killrvideo.spacecraft_journey_catalog WHERE spacecraft_name='Crew Dragon Endeavour,SpaceX' AND journey_id=8dfd0a30-c73b-11ea-b87b-1325d5aaa06b;
	 */
	public void landing_journey() {
		// When
		journeyRepo.landing(
			UUID.fromString(this.TEST_JOURNEYID),
			this.TEST_SPACECRAFT
		);
		LOGGER.info("Journey {} has now landed", this.TEST_JOURNEYID);
		LOGGER.info("SUCCESS");
	}
}
