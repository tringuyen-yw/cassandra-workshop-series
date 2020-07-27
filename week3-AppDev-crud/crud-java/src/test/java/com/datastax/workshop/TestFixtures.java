package com.datastax.workshop;

import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Common setup for all tests
 */
public class TestFixtures {
    /** Logger for the class. */
    protected static Logger STATICLOGGER = LoggerFactory.getLogger(TestFixtures.class);
   
    /** Connect once for all tests. */
    protected static CqlSession cqlSession;
    
    /** Use the Repository Pattern. */
    protected static JourneyRepository journeyRepo;

    // common logger used by test class instance
    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // Common journey data for all tests
    protected String TEST_SPACECRAFT     = "Crew Dragon Endeavour,SpaceX";
    protected String TEST_JOURNEYID      = "8dfd0a30-c73b-11ea-b87b-1325d5aaa06b";
    protected String TEST_JOURNEYSUMMARY = "Bring Astronauts to ISS";


    @BeforeAll
    public static void initConnection() {
        STATICLOGGER.info("========================================");
        STATICLOGGER.info("Init CqlSession");
        //TestUtils.createKeyspaceForLocalInstance();
        cqlSession = CqlSession.builder()
            .withCloudSecureConnectBundle(Paths.get(DBConnection.SECURE_CONNECT_BUNDLE))
            .withAuthCredentials(DBConnection.USERNAME, DBConnection.PASSWORD)
            .withKeyspace(DBConnection.KEYSPACE)
            .build();
            STATICLOGGER.info("Init JourneyRepository");
            journeyRepo = new JourneyRepository(cqlSession);
    }

    @AfterAll
    public static void closeConnectionToCassandra() {
        if (null != cqlSession) {
            STATICLOGGER.info("Closing CqlSession");
            STATICLOGGER.info("========================================");
            cqlSession.close();
        }
    }
}