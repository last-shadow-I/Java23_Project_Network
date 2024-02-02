package ru.teamscore.java23.network.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.network.model.enums.LineType;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

  Network networkTest = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.255.0"), new IpAddress("10.15.0.0"));
  IpAddress[] ipAddresses = new IpAddress[10];

  {
    for (int i = 1; i < 11; i++) {
      ipAddresses[i-1] = new IpAddress("192.168.0." + i);
      networkTest.addHost(ipAddresses[i-1], i + "");
    }
    networkTest.addCommunicationLine("1", LineType.WIRED);
    networkTest.getCommunicationLine("1", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[0]));
    networkTest.getCommunicationLine("1", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[1]));
    networkTest.getCommunicationLine("1", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[2]));
    networkTest.addCommunicationLine("2", LineType.WIRED);
    networkTest.getCommunicationLine("2", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[1]));
    networkTest.getCommunicationLine("2", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[2]));
    networkTest.addCommunicationLine("3", LineType.WIRED);
    networkTest.getCommunicationLine("3", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[2]));
    networkTest.getCommunicationLine("3", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[3]));
    networkTest.addCommunicationLine("4", LineType.WIRED);
    networkTest.getCommunicationLine("4", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[3]));
    networkTest.getCommunicationLine("4", LineType.WIRED).addHost(networkTest.getHost(ipAddresses[4]));
  }


  @Test
  void addHost() {
    assertEquals(10, networkTest.getAllHosts().size());
    networkTest.addHost("knn");
    assertEquals(11, networkTest.getAllHosts().size());
    networkTest.addHost(new IpAddress("192.168.0.100"), "habwjd");
    assertEquals(12, networkTest.getAllHosts().size());
  }

  @Test
  void removeHost() {
    assertEquals(10, networkTest.getAllHosts().size());
    networkTest.removeHost(ipAddresses[1]);
    assertEquals(9, networkTest.getAllHosts().size());
    networkTest.removeHost(ipAddresses[1]);
    assertEquals(9, networkTest.getAllHosts().size());
    networkTest.removeHost(new IpAddress("192.168.0.1"));
    assertEquals(8, networkTest.getAllHosts().size());
    networkTest.removeHost(networkTest.getHost(new IpAddress("192.168.0.5")));
    assertEquals(7, networkTest.getAllHosts().size());
  }

  @Test
  void getUnusedIpAddress() {
    Network network = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.255.0"), new IpAddress("10.15.0.0"));
    for (int i = 0; i < 1000; i++) {
      assertTrue(network.isValidIpAddress(network.getUnusedIpAddress()));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"192.168.0.1", "192.168.127.255", "192.168.0.254"})
  void isValidIpAddressTrue(String value) {
    Network network = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.128.0"), new IpAddress("10.15.0.0"));
    assertTrue(network.isValidIpAddress(new IpAddress(value)));

    Network network2 = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.0.0"), new IpAddress("10.15.0.0"));
    assertTrue(network2.isValidIpAddress(new IpAddress(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"255.255.255.255", "0.0.0.0", "192.165.128.0", "192.167.0.0"})
  void isValidIpAddressFalse(String value) {
    Network network = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.128.0"), new IpAddress("10.15.0.0"));
    assertFalse(network.isValidIpAddress(new IpAddress(value)));

    Network network2 = new Network(new IpAddress("192.168.0.0"), new IpAddress("255.255.0.0"), new IpAddress("10.15.0.0"));
    assertFalse(network2.isValidIpAddress(new IpAddress(value)));
  }

  @Test
  void addCommunicationLine() {
    assertEquals(4, networkTest.getAllCommunicationLines().size());
    networkTest.addCommunicationLine("5", LineType.WIRED);
    assertEquals(5, networkTest.getAllCommunicationLines().size());
  }

  @Test
  void removeCommunicationLine() {
    assertEquals(4, networkTest.getAllCommunicationLines().size());
    networkTest.removeCommunicationLine("4", LineType.WIRED);
    assertEquals(3, networkTest.getAllCommunicationLines().size());
    networkTest.removeCommunicationLine("4", LineType.WIRED);
    assertEquals(3, networkTest.getAllCommunicationLines().size());
    networkTest.removeCommunicationLine("3", LineType.WIRED);
    assertEquals(2, networkTest.getAllCommunicationLines().size());
  }

  @Test
  void testRemoveCommunicationLine() {
    assertEquals(4, networkTest.getAllCommunicationLines().size());
    networkTest.removeCommunicationLine(networkTest.getCommunicationLine("1", LineType.WIRED));
    assertEquals(3, networkTest.getAllCommunicationLines().size());
    networkTest.removeCommunicationLine(networkTest.getCommunicationLine("2", LineType.WIRED));
    assertEquals(2, networkTest.getAllCommunicationLines().size());
  }

  @Test
  void getUnreachableHosts() {
    System.out.println(networkTest.getUnreachableHosts());
    assertEquals(5, networkTest.getUnreachableHosts().size());
  }

  @Test
  void isAddressHostTrue() {
    for (var i: ipAddresses) {
      assertTrue(networkTest.isAddressHost(i));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"255.255.255.255", "0.0.0.0", "192.165.128.0", "192.167.0.0"})
  void isAddressHostFalse(String value) {
    assertFalse(networkTest.isAddressHost(new IpAddress(value)));
  }

  @Test
  void getHostsLinkedDirectlyTest() {
    System.out.println(networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[2])));
    assertEquals(3, networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[2])).size());
    System.out.println(networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[4])));
    assertEquals(1, networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[4])).size());
    System.out.println(networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[5])));
    assertEquals(0, networkTest.getHostsLinkedDirectly(networkTest.getHost(ipAddresses[5])).size());
  }

  @Test
  void getAvailableHosts() {
    System.out.println(networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[0])));
    assertEquals(2, networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[0])).size());
    System.out.println(networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[4])));
    assertEquals(3, networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[4])).size());
    System.out.println(networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[5])));
    assertEquals(0, networkTest.getAvailableHosts(networkTest.getHost(ipAddresses[5])).size());
  }
}