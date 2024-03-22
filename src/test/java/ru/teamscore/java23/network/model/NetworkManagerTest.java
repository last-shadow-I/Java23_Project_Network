package ru.teamscore.java23.network.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.network.model.entities.*;
import ru.teamscore.java23.network.model.enums.LineType;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NetworkManagerTest {

  private static EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private NetworkManager networkManager;

  @BeforeAll
  public static void setup() throws IOException {
    entityManagerFactory = new Configuration()
            .configure("hibernate-postgres.cfg.xml")
            .addAnnotatedClass(NetworkManager.class)
            .addAnnotatedClass(Host.class)
            .addAnnotatedClass(Network.class)
            .addAnnotatedClass(CommunicationLine.class)
            .addAnnotatedClass(LineHost.class)
            .buildSessionFactory();
    SqlScripts.runFromFile(entityManagerFactory, "createSchema.sql");
    SqlScripts.runFromFile(entityManagerFactory, "insertTestNetwork.sql");
  }

  @AfterAll
  public static void tearDown() {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }

  @BeforeEach
  public void openSession() throws IOException {
    SqlScripts.runFromFile(entityManagerFactory, "insertTestHosts.sql");
    SqlScripts.runFromFile(entityManagerFactory, "insertTestCommunicationLine.sql");
    entityManager = entityManagerFactory.createEntityManager();
    networkManager = new NetworkManager(entityManager);
  }

  @AfterEach
  public void closeSession() throws IOException {
    if (entityManager != null) {
      entityManager.close();
    }
    SqlScripts.runFromFile(entityManagerFactory, "clearTestCommunicationLines.sql");
    SqlScripts.runFromFile(entityManagerFactory, "clearTestHosts.sql");
  }

  @Test
  void getNetwork() {
    var network = networkManager.getNetwork();
    assertNotNull(network);
  }

  @Test
  void updateNetwork() {
    IpAddress newMask = new IpAddress("255.255.0.0");
    IpAddress newGateway = new IpAddress("10.1.0.2");
    // get network
    Network network = networkManager.getNetwork();
    // change network
    network.setNetworkMask(newMask);
    network.setGateway(newGateway);
    // save changes
    networkManager.updateNetwork(network);
    // reload from DB and assert changes
    var networkAfterUpdate = networkManager.getNetwork();
    assertNotNull(networkAfterUpdate);
    assertEquals(newMask, networkAfterUpdate.getNetworkMask());
    assertEquals(newGateway, networkAfterUpdate.getGateway());
  }

  @Test
  void getLinesCount() {
    assertEquals(10, networkManager.getLinesCount());
  }

  @Test
  void getLinesAll() {
    var linesAll = networkManager.getLinesAll();
    assertEquals(10, linesAll.length);
    for (int i = 1; i <= linesAll.length; i++) {
      int finalId = i;
      assertTrue(Arrays.stream(linesAll).anyMatch(o -> o.getId() == finalId),
              finalId + " id is missing");
    }
  }

  @ParameterizedTest
  @ValueSource(longs = {1, 5, 7, 3, 9})
  void getLine(long id) {
    var line = networkManager.getLine(id);
    assertTrue(line.isPresent());
    assertEquals(id, line.get().getId());
  }

  @Test
  void addLine() {
    // get network
    Network network = networkManager.getNetwork();

    CommunicationLine lineToAdd = network.addCommunicationLine("Test", LineType.WIRED);

    // save line
    long count = networkManager.getLinesCount();
    networkManager.addLine(lineToAdd);
    assertEquals(count + 1, networkManager.getLinesCount());
    // assert
    var addedLine = networkManager.getLine(lineToAdd.getId());
    assertTrue(addedLine.isPresent());
    assertEquals(lineToAdd.getLineName(), addedLine.get().getLineName());
    assertEquals(lineToAdd.getType(), addedLine.get().getType());
  }

  @Test
  void updateLine() {
    long lineId = 5;
    LineType newLineType = LineType.WIRELESS;
    String newLineName = "TEST TEST";
    // change line in network
    var lineToEdit = networkManager.getLine(lineId);
    assertTrue(lineToEdit.isPresent(), "Line with id = " + lineId + " should exist in network");
    // change line
    lineToEdit.get().setLineName(newLineName);
    lineToEdit.get().setType(newLineType);
    // save changes
    networkManager.updateLine(lineToEdit.get());
    // reload from DB and assert changes
    var lineAfterUpdate = networkManager.getLine(lineId);
    assertTrue(lineAfterUpdate.isPresent(), "Line with id = " + lineId + " disappeared after update");
    assertEquals(newLineName, lineAfterUpdate.get().getLineName());
    assertEquals(newLineType, lineAfterUpdate.get().getType());
  }

  @Test
  void removeLine() {
    long lineId = 6;
    // delete line in network
    var lineToRemove = networkManager.getLine(lineId);
    assertTrue(lineToRemove.isPresent(), "Line with id = " + lineId + " should exist in network");
    // remove line
    networkManager.removeLine(lineToRemove.get());
    // reload from DB and assert changes
    assertTrue((networkManager.getLine(lineId)).isEmpty());
  }
}