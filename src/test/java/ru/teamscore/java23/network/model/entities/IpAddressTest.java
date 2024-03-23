package ru.teamscore.java23.network.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.teamscore.java23.network.model.exceptions.WrongIntIpAddressException;
import ru.teamscore.java23.network.model.exceptions.WrongStringToIpAddressException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.teamscore.java23.network.model.entities.IpAddress.isValid;

class IpAddressTest {

  @ParameterizedTest
  @ValueSource(strings = {"126.12.100.0", "255.255.255.255", "0.0.0.0", "128.12.74.18", "127.80.80.0"})
  void isValidTrue(String value) {
    assertTrue(isValid(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-126.12.100.0", "126.12.100", "126.12.100.256", "126.12.100.255.0"})
  void isValidWrongIntIpAddressException(String value) {
    assertThrows(WrongIntIpAddressException.class, () -> isValid(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "фыв"})
  void isValidWrongStringToIpAddressException(String value) {
    assertThrows(WrongStringToIpAddressException.class, () -> isValid(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"126.12.100.0", "255.255.255.255", "0.0.0.0", "128.12.74.18", "127.80.80.0"})
  void testToString(String value) {
    IpAddress ipAddress = new IpAddress(value);
    assertEquals(value.trim(), ipAddress.toString());
  }

  @Test
  void testEqualsTrue() {
    String value = "126.12.100.0";
    IpAddress ipAddress = new IpAddress(value);
    IpAddress ipAddress1 = new IpAddress(value);
    assertEquals(ipAddress, ipAddress1);
    assertEquals(ipAddress1, ipAddress);
  }

  @Test
  void testEqualsFalse() {
    String value1 = "126.12.100.0";
    String value2 = "255.255.255.255";
    IpAddress ipAddress1 = new IpAddress(value1);
    IpAddress ipAddress2 = new IpAddress(value2);
    assertNotEquals(ipAddress1, ipAddress2);
    assertNotEquals(ipAddress2, ipAddress1);
    assertNotEquals(ipAddress1, value1);
  }
}