package ru.teamscore.java23.network.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.teamscore.java23.network.model.enums.LineType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode
public class CommunicationLine {

  @Getter
  @Setter
  private String lineName;

  private final List<Host> hosts;

  @Getter
  @Setter
  private LineType type;

  public CommunicationLine(String lineName, LineType type) {
    this.lineName = lineName;
    this.type = type;
    this.hosts = new ArrayList<>();
  }

  public void addHost(Host host){

    Host existingHost = getHost(host.getIpAddress());

    if (existingHost == null) {
      hosts.add(host);
    }
  }

  public void removeHost(Host host){
    Host existingHost = getHost(host.getIpAddress());

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
  }

  public void removeHost(IpAddress ipAddress){
    Host existingHost = getHost(ipAddress);

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
  }

  public Host getHost(IpAddress ipAddress){
    return hosts.stream()
            .filter(e -> e.getIpAddress().equals(ipAddress))
            .findFirst()
            .orElse(null);
  }

  public Collection<Host> getAllHosts(){
    return hosts.stream().toList();
  }
}
