package com.datastax.workshop;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Let's play !
 */ 
@RunWith(JUnitPlatform.class)
public class Ex04_Query5b_TakeOff extends TestFixtures implements DataModelConstants {
    
    /**
     * CHECK on Astra CQL Console:
     * Must see: active=true, 'start' has valid timestamp
     * SELECT * FROM killrvideo.spacecraft_journey_catalog WHERE spacecraft_name='Crew Dragon Endeavour,SpaceX' AND journey_id=8dfd0a30-c73b-11ea-b87b-1325d5aaa06b; 
     */
    @Test
    public void takeoff_the_spacecraft() {
        LOGGER.info("9..8..7..6..5..4..3..2..1 Ignition");
        journeyRepo.takeoff(
            this.TEST_SPACECRAFT,
            UUID.fromString(this.TEST_JOURNEYID),
            this.TEST_JOURNEYSUMMARY + " (takeoff)"
        );
        LOGGER.info("Journey {} has now taken off", this.TEST_JOURNEYID);
        LOGGER.info("SUCCESS");
    }
    
}
