package ru.teamscore.java23.network.model.exceptions;

import lombok.Getter;
import ru.teamscore.java23.network.model.entities.IpAddress;

public class IpAddressNotMatchNetwork  extends RuntimeException{
  @Getter
  private final IpAddress ipAddress;

  public IpAddressNotMatchNetwork(String message, IpAddress ipAddress) {
    super(message);
    this.ipAddress = ipAddress;
  }
}
