package ru.teamscore.java23.network.model.entities;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Host {

  @Getter
  @Setter
  private IpAddress ipAddress;
  @Getter
  @Setter
  private String macAddress;
}
