package ru.teamscore.java23.network.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.teamscore.java23.network.model.exceptions.WrongIntIpAddressException;
import ru.teamscore.java23.network.model.exceptions.WrongStringToIpAddressException;

@EqualsAndHashCode
@NoArgsConstructor
public class IpAddress {

  @Getter
  private int[] intIpAddress;

  public IpAddress(@NonNull String ipAddress){

    int[] intIpAddress = ipToInt(ipAddress);

    if (isValid(intIpAddress)) {
      setIntIpAddress(intIpAddress);
    }
  }

  public static IpAddress of(int[] intIpAddress){
    if (isValid(intIpAddress)){
      IpAddress ipAddress = new IpAddress();
      ipAddress.setIntIpAddress(intIpAddress);
      return ipAddress;
    }
    return null;
  }

  private static int[] ipToInt(@NonNull String ipAddress){
    String[] ipAddressInArray = ipAddress.split("\\.");

    int[] intIpAddress = new int[ipAddressInArray.length];
    for (int i = 0; i < ipAddressInArray.length; i++) {
      try {
        intIpAddress[i] = Integer.parseInt(ipAddressInArray[i]);
      }
      catch (NumberFormatException e){
        throw new WrongStringToIpAddressException("Невозможно преобразовать строку в ip Address", ipAddress);
      }
    }
    return intIpAddress;
  }

  public static boolean isValid(int @NonNull [] intIpAddress){

    if (intIpAddress.length != 4){
      throw new WrongIntIpAddressException("Неверный размер ip адреса: ", intIpAddress);
    }

    for (int i : intIpAddress) {
      if (i < 0 || i > 255){
        throw new WrongIntIpAddressException("Не ip адрес: ", intIpAddress);
      }
    }
    return true;
  }

  public static boolean isValid(@NonNull String ipAddress) {
    return isValid(ipToInt(ipAddress));
  }

  private void setIntIpAddress(int[] newIntIpAddress) {
    intIpAddress = newIntIpAddress;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < intIpAddress.length-1; i++) {
      stringBuilder.append(intIpAddress[i]);
      stringBuilder.append(".");
    }
    stringBuilder.append(intIpAddress[intIpAddress.length-1]);
    return stringBuilder.toString();
  }
}
