package ru.teamscore.java23.network.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
        System.err.println(e.getMessage());
        throw e;
      }

    }
    return intIpAddress;
  }

  public static boolean isValid(@NonNull int[] intIpAddress){

    if (intIpAddress.length != 4){
      System.err.println("Неверный размер ip адреса: " + intIpAddress.length);
      return false;
    }

    for (int i : intIpAddress) {
      if (i < 0 || i > 255){
        System.err.println("Не ip адрес: " + i);
        return false;
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
