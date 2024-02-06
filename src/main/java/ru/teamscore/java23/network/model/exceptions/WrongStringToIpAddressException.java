package ru.teamscore.java23.network.model.exceptions;

import lombok.Getter;

public class WrongStringToIpAddressException extends NumberFormatException {
  @Getter
  private final String ipAddress;

  public WrongStringToIpAddressException(String message, String ipAddress) {
    super(message);
    this.ipAddress = ipAddress;
  }
}
