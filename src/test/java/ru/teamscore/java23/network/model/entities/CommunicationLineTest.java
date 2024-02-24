package ru.teamscore.java23.network.model.entities;

import org.junit.jupiter.api.Test;
import ru.teamscore.java23.network.model.enums.LineType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommunicationLineTest {

  Host host1 = new Host(new IpAddress("127.0.0.0"), "aiudhiuhhuahw123");
  Host host2 = new Host(new IpAddress("127.0.0.1"), "isbildf/787676");
  Host host3 = new Host(new IpAddress("127.0.0.2"), "1k2h34khgop89");
  Host host4 = new Host(new IpAddress("127.0.0.3"), "ajksdkhk");
  Host host5 = new Host(new IpAddress("127.0.0.4"), "1k2h3ghawdc;a;w4khgop89");

  CommunicationLine communicationLine1 = new CommunicationLine("Test1", LineType.WIRED);
  CommunicationLine communicationLine2 = new CommunicationLine("Test2", LineType.WIRELESS);

  {
    communicationLine1.addHost(host5);

    communicationLine2.addHost(host4);
    communicationLine2.addHost(host2);
  }


  @Test
  void addHost() {
    communicationLine1.addHost(host1);
    assertEquals(2, communicationLine1.getAllHosts().size());
    communicationLine1.addHost(host2);
    assertEquals(3, communicationLine1.getAllHosts().size());
    communicationLine1.addHost(host2);
    assertEquals(3, communicationLine1.getAllHosts().size());
    communicationLine1.addHost(host3);
    assertEquals(4, communicationLine1.getAllHosts().size());
  }

  @Test
  void removeHost() {
    communicationLine1.removeHost(host1);
    assertEquals(1, communicationLine1.getAllHosts().size());
    communicationLine1.removeHost(host5);
    assertEquals(0, communicationLine1.getAllHosts().size());

    communicationLine2.removeHost(host4);
    assertEquals(1, communicationLine2.getAllHosts().size());
  }

  @Test
  void testRemoveHost() {
    communicationLine1.removeHost(new IpAddress("127.0.0.0"));
    assertEquals(1, communicationLine1.getAllHosts().size());
    communicationLine1.removeHost(new IpAddress("127.0.0.4"));
    assertEquals(0, communicationLine1.getAllHosts().size());

    communicationLine2.removeHost(new IpAddress("127.0.0.3"));
    assertEquals(1, communicationLine2.getAllHosts().size());
  }

  @Test
  void getHost() {
    assertNull(communicationLine1.getHost(host1.getIpAddress()));
    assertEquals(host5, communicationLine1.getHost(host5.getIpAddress()));
  }
}