package com.datastax.workshop;

import java.net.InetSocketAddress;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Singleton class providing various utilities method
 * Ex: Singleton.getInstance().createKeyspaceForLocalInstance(); 
 */
public class TestUtils {
  private static TestUtils single_instance = null; 

  private static Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
  
  // private constructor to prevent instantiation of this singleton class
  private TestUtils() { }

  public static TestUtils getInstance( ) {
    if (single_instance == null) 
      single_instance = new TestUtils(); 

    return single_instance; 
  }
  
  public void createKeyspaceForLocalInstance() {
      try (CqlSession cqlSession = CqlSession.builder()
              .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
              .withLocalDatacenter("datacenter1")
              .build()) {
          cqlSession.execute(SchemaBuilder.createKeyspace("killrvideo")
                  .ifNotExists().withSimpleStrategy(1)
                  .withDurableWrites(true).build());
      }
  }
  
  /**
   * Init an Astra Connection info from src/test/resources/application-test.properties
   * This method doesn't attempt to connect to Astra
   */
  public AstraConnectionInfo getAstraDBConnectInfo() {
    final String configFileName = "application-test.properties";
    InputStream inputStrm;
    AstraConnectionInfo connectInfo = null;

    try {
      Properties prop = new Properties();
      inputStrm = TestUtils.class.getClassLoader().getResourceAsStream(configFileName);

      if (inputStrm == null) {
        String errMsg = "Missing test resources file: src/test/resources/" + configFileName;
        LOGGER.error(errMsg);
        throw new FileNotFoundException(errMsg);
      }

      // When
      //load a properties file from class path, inside static method
      prop.load(inputStrm);
      //prop.list(System.out); // list all key-value pairs

      String secureBundleFilename = prop.getProperty("SECURE_CONNECT_BUNDLE_FILE");
      String userName = prop.getProperty("USERNAME");
      String clearPwd = prop.getProperty("PASSWORD");
      String keyspaceName = prop.getProperty("KEYSPACE");
    
      connectInfo = new AstraConnectionInfo(secureBundleFilename, userName, clearPwd, keyspaceName);
      inputStrm.close();

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return connectInfo;
  }
}
