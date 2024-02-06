package ru.teamscore.java23.network.model.exceptions;

import lombok.Getter;

public class WrongIntIpAddressException extends RuntimeException {

  @Getter
  private final int[] ipAddress;

  public WrongIntIpAddressException(String message, int[] ipAddress) {
    super(message);
    this.ipAddress = ipAddress;
  }
}


