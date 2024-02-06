package ru.teamscore.java23.network.model.exceptions;

import lombok.Getter;
import ru.teamscore.java23.network.model.entities.Host;

public class ExistHostException extends RuntimeException{
  @Getter
  private final Host host;

  public ExistHostException(String message, Host host) {
    super(message);
    this.host = host;
  }
}
