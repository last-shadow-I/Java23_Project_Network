package ru.teamscore.java23.network.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.teamscore.java23.network.model.enums.LineType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "communication_line", schema = "communication_lines")
public class CommunicationLine {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter
  @Setter
  @Column(name = "line_name", nullable = false, length = 20)
  private String lineName;

  @Getter
  @Setter
  @Enumerated
  @Column(name = "type", nullable = false, length = 12)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private LineType type;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "network_id", nullable = false)
  private Network network;

  @OneToMany(mappedBy = "pk.communicationLine", cascade = CascadeType.ALL)
  private final List<LineHost> hosts = new ArrayList<>();

  public LineHost addHost(@NonNull Host host){

    var existingHost = getHost(host);

    if (existingHost == null) {
      LineHost lineHost = new LineHost(host, this);
      hosts.add(lineHost);
      return lineHost;
    }
    return existingHost;
  }

  public LineHost removeHost(Host host){
    var existingHost = getHost(host);

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
    return existingHost;
  }

  public LineHost removeHost(IpAddress ipAddress){
    var existingHost = getHost(ipAddress);

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
    return existingHost;
  }

  public LineHost removeHost(String ipAddress){
    return removeHost(new IpAddress(ipAddress));
  }

  private LineHost getHost(@NonNull IpAddress ipAddress) {
    return hosts.stream()
            .filter(e -> e.getHost().getIpAddress().equals(ipAddress))
            .findFirst()
            .orElse(null);
  }

  public LineHost getHost(@NonNull String ipAddress) {
    return getHost(new IpAddress(ipAddress));
  }

  public LineHost getHost(@NonNull Host host) {
    return hosts.stream()
            .filter(e -> e.getHost().equals(host))
            .findFirst()
            .orElse(null);
  }

  public LineHost getHost(long hostId){
    return hosts.stream()
            .filter(e -> e.getHost().getId() == hostId)
            .findFirst()
            .orElse(null);
  }

  public Collection<LineHost> getAllHosts(){
    return hosts.stream().toList();
  }

  public int getHostsCount(){
    return hosts.size();
  }
}
