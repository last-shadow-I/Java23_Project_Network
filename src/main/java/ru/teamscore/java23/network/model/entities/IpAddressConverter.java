package ru.teamscore.java23.network.model.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IpAddressConverter implements AttributeConverter<IpAddress, String> {

  @Override
  public String convertToDatabaseColumn(IpAddress ipAddress) {
    return ipAddress.toString();
  }

  @Override
  public IpAddress convertToEntityAttribute(String dbData) {
    return new IpAddress(dbData);
  }
}
