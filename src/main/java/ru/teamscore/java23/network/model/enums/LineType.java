package ru.teamscore.java23.network.model.enums;

import lombok.Getter;

public enum LineType {
  WIRED("Проводная"),
  WIRELESS("Беспроводная");

  @Getter
  private final String title;

  LineType(String title) {
    this.title = title;
  }
}
