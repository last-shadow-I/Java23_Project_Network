package ru.teamscore.java23.network.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "host", schema = "hosts")
@NamedQuery(name = "hostsCount", query = "select count(*) from Host")
@NamedQuery(name = "hostByIpAddress", query = "from Host h where h.ipAddress = :ipAddress")
public class Host {
  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter
  @Setter
  @Column(name = "ip_address", nullable = false, unique = true, length = 15)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Convert(converter = IpAddressConverter.class)
  private IpAddress ipAddress;

  @Getter
  @Setter
  @Column(name = "mac_address", nullable = false, unique = true, length = 17)
  private String macAddress;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "network_id", nullable = false)
  private Network network;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Host host)) return false;
    return Objects.equals(id, host.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Host{" +
            "id=" + id +
            ", ipAddress=" + ipAddress +
            ", macAddress='" + macAddress + '\'' +
            ", networkId=" + network.getNetworkId() +
            '}';
  }
}
