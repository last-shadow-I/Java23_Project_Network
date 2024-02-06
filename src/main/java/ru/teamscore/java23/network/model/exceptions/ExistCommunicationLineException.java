package ru.teamscore.java23.network.model.exceptions;

import lombok.Getter;
import ru.teamscore.java23.network.model.entities.CommunicationLine;
public class ExistCommunicationLineException extends RuntimeException{
  @Getter
  private final CommunicationLine communicationLine;

  public ExistCommunicationLineException(String message, CommunicationLine communicationLine) {
    super(message);
    this.communicationLine = communicationLine;
  }
}
