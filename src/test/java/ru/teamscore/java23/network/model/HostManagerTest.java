package ru.teamscore.java23.network.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.network.model.entities.*;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HostManagerTest {

  private static EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private HostManager hostManager;

  @BeforeAll
  public static void setup() throws IOException {
    entityManagerFactory = new Configuration()
            .configure("hibernate-postgres.cfg.xml")
            .addAnnotatedClass(HostManager.class)
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
    entityManager = entityManagerFactory.createEntityManager();
    hostManager = new HostManager(entityManager);
  }

  @AfterEach
  public void closeSession() throws IOException {
    if (entityManager != null) {
      entityManager.close();
    }
    SqlScripts.runFromFile(entityManagerFactory, "clearTestHosts.sql");
  }

  @Test
  void getHostsCount() {
    assertEquals(20, hostManager.getHostsCount());
  }

  @Test
  void getHostsAll() {
    var hostsAll = hostManager.getHostsAll();
    assertEquals(20, hostsAll.length);
    for (int i = 1; i <= hostsAll.length; i++) {
      int finalId = i;
      assertTrue(Arrays.stream(hostsAll).anyMatch(o -> o.getId() == finalId),
              finalId + " id is missing");
    }
  }

  @ParameterizedTest
  @ValueSource(longs = {1, 5, 15, 7, 10})
  void getHostId(long id) {
    var host = hostManager.getHost(id);
    assertTrue(host.isPresent());
    assertEquals(id, host.get().getId());
  }

  @ParameterizedTest
  @ValueSource(strings = {"192.168.0.1", "192.168.0.5", "192.168.0.15", "192.168.0.7", "192.168.0.10"})
  void testGetHostIpAddressString(String ipAddress) {
    var host = hostManager.getHost(ipAddress);
    assertTrue(host.isPresent());
    assertEquals(ipAddress, host.get().getIpAddress().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"192.168.0.1", "192.168.0.5", "192.168.0.15", "192.168.0.7", "192.168.0.10"})
  void testGetHostIpAddress(String ipAddress) {
    var host = hostManager.getHost(new IpAddress(ipAddress));
    assertTrue(host.isPresent());
    assertEquals(ipAddress, host.get().getIpAddress().toString());
  }

  @Test
  void addHost() {
    NetworkManager networkManager = new NetworkManager(entityManager);
    Network network = networkManager.getNetwork();
    Host[] hosts = new Host[]{
            new Host(0, network.getUnusedIpAddress(), "3B:56:4F:11:8B:0A", network),
            new Host(0, network.getUnusedIpAddress(), "3B:56:4F:11:8B:0C", network)
    };

    long startCount = hostManager.getHostsCount();
    assertTrue(hostManager.getHost(hosts[0].getIpAddress()).isEmpty(), "Host not exists before add");
    assertTrue(hostManager.getHost(hosts[1].getIpAddress()).isEmpty(), "Host not exists before add");
    hostManager.addHost(hosts[0]);
    assertEquals(startCount + 1, hostManager.getHostsCount(), "Host added");
    assertTrue(hostManager.getHost(hosts[0].getIpAddress()).isPresent());
    hostManager.addHost(hosts[0]);
    assertEquals(startCount + 1, hostManager.getHostsCount(), "Host not added, already exists");
    hostManager.addHost(hosts[1]);
    assertEquals(startCount + 2, hostManager.getHostsCount(), "Host added");
  }

  @Test
  void updateHost() {
    long hostId = 1;
    String newMacAddress = "3B:56:4F:11:8B:0C";
    // get some host from DB
    var existingHost = hostManager.getHost(hostId);
    assertTrue(existingHost.isPresent(), "Host with id = " + hostId + " should exist");
    Host host = existingHost.get();
    // change host and save to DB
    host.setMacAddress(newMacAddress);
    hostManager.updateHost(host);
    // reload host from DB again and assure it changed
    var hostAfterUpdate = hostManager.getHost(hostId);
    assertTrue(hostAfterUpdate.isPresent(), "Host with id = " + hostId + " disappeared after update");
    assertEquals(host.getMacAddress(), hostAfterUpdate.get().getMacAddress());

    assertSame(host, hostAfterUpdate.get());
    assertNotSame(existingHost, hostAfterUpdate);
  }

  @Test
  void removeHost() {
    NetworkManager networkManager = new NetworkManager(entityManager);
    Network network = networkManager.getNetwork();
    Host[] hosts = new Host[]{
            new Host(0, network.getUnusedIpAddress(), "3B:56:4F:11:8B:0A", network),
    };

    long startCount = hostManager.getHostsCount();
    assertTrue(hostManager.getHost(hosts[0].getIpAddress()).isEmpty(), "Host not exists");

    hostManager.removeHost(hosts[0]);
    assertEquals(startCount, hostManager.getHostsCount(), "Host not removed");

    hostManager.addHost(hosts[0]);
    assertEquals(startCount + 1, hostManager.getHostsCount(), "Host added");
    hostManager.removeHost(hosts[0]);
    assertEquals(startCount, hostManager.getHostsCount(), "Host removed");
    assertTrue(hostManager.getHost(hosts[0].getIpAddress()).isEmpty(), "Host not exists");
    assertThrows(jakarta.persistence.OptimisticLockException.class, () -> hostManager.removeHost(hosts[0]));
    assertEquals(startCount, hostManager.getHostsCount(), "Host not removed");
  }

  @Test
  void find() {
    var result = hostManager.find("192");
    assertEquals(20, result.size());
    result = hostManager.find("9A");
    assertEquals(10, result.size());
    result = hostManager.find("38");
    assertEquals(1, result.size());
  }
}