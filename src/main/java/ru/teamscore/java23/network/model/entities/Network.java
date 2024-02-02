package ru.teamscore.java23.network.model.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.teamscore.java23.network.model.enums.LineType;

import java.util.*;

public class Network {

  @Getter
  private IpAddress ipAddress;
  @Getter
  @Setter
  private IpAddress networkMask;
  @Getter
  @Setter
  private IpAddress gateway;

  private final List<Host> hosts;

  private final List<CommunicationLine> communicationLines;

  public Network(IpAddress ipAddress, IpAddress networkMask, IpAddress gateway) {
    this.ipAddress = ipAddress;
    this.networkMask = networkMask;
    this.gateway = gateway;
    this.hosts = new ArrayList<>();
    this.communicationLines = new ArrayList<>();
  }

  // по умолчанию предлагается случайный адрес, соответствующий маске сети
  public Host addHost(String macAddress){
    Host host = new Host(getUnusedIpAddress(), macAddress);
    hosts.add(host);
    return host;
  }

  public void addHost(IpAddress ipAddress, String macAddress){
    if(isValidIpAddress(ipAddress)){
      Host existingHost = getHost(getIpAddress());

      if (existingHost == null) {
        hosts.add(new Host(ipAddress, macAddress));
      } else {
        System.err.println("Такой хост уже существует");
      }
    } else {
      System.err.println("Ip адрес не подходит данной сети");
    }
  }

  public void removeHost(@NonNull Host host){
    removeHost(host.getIpAddress());
  }

  public void removeHost(@NonNull IpAddress ipAddress){
    Host existingHost = getHost(ipAddress);

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
  }

  // получить случайный адрес, соответствующий маске сети
  public IpAddress getUnusedIpAddress(){

    int startIntAddress = ipToInteger(getIpAddress().getIntIpAddress());
    int maskIntAddress = ipToInteger(networkMask.getIntIpAddress());

    int endIntAddress = startIntAddress | ~maskIntAddress; // получаем широковещательный (последний) адрес сети

    while (true){
      IpAddress unusedIp = new IpAddress(integerToIp(startIntAddress + (int) (Math.random() * (endIntAddress-startIntAddress))));
      if (isValidIpAddress(unusedIp)){
        return unusedIp;
      }
    }
  }

  // Переводим ip адрес в битовый вид
  private static int ipToInteger(@NonNull int[] intIpAddress){
    return ((intIpAddress[0]*256 + intIpAddress[1])*256 + intIpAddress[2])*256 + intIpAddress[3];
  }

  private static String integerToIp(int i) {
    return ((i >> 24 ) & 0xFF) + "." +
            ((i >> 16 ) & 0xFF) + "." +
            ((i >>  8 ) & 0xFF) + "." +
            ( i & 0xFF);
  }

  // подпадает ли введенный адрес под адрес и маску сети;
  public boolean isValidIpAddress(@NonNull IpAddress checkIpAddress){

    if(checkIpAddress.getIntIpAddress().length != 4){
      return false;
    }

    for (int i = 0; i < getNetworkMask().getIntIpAddress().length; i++) {
      if((checkIpAddress.getIntIpAddress()[i] & networkMask.getIntIpAddress()[i]) != getIpAddress().getIntIpAddress()[i]){
        return false;
      }
    }
    return true;
  }

  public void addCommunicationLine(String lineName, LineType type){

    CommunicationLine existingCommunicationLine = getCommunicationLine(lineName, type);

    if (existingCommunicationLine == null) {
      communicationLines.add(new CommunicationLine(lineName, type));
    } else {
      System.err.println("Такая линия связи уже существует");
    }
  }

  public void removeCommunicationLine(@NonNull CommunicationLine communicationLine){
    removeCommunicationLine(communicationLine.getLineName(), communicationLine.getType());
  }

  public void removeCommunicationLine(String lineName, LineType type){
    CommunicationLine existingCommunicationLine = getCommunicationLine(lineName, type);

    if (existingCommunicationLine != null) {
      communicationLines.remove(existingCommunicationLine);
    }
  }

  // количество хостов
  public int getCountHosts(){
    return hosts.size();
  }

  // количество связей,
  public int getCountCommunicationLine(){
    return communicationLines.size();
  }

  // есть ли вообще такой хост в сети
  public boolean isAddressHost(@NonNull IpAddress ipAddress){
    var result = hosts.stream()
            .filter(e -> e.getIpAddress().equals(ipAddress))
            .findFirst()
            .orElse(null);
    return result != null;
  }

  // список хостов, с которыми он связан напрямую
  public Collection<Host> getHostsLinkedDirectly(@NonNull Host host){
    Set<Host> hostSet = new HashSet<>();
    for (var l: communicationLines) {
      if(l.getHost(host.getIpAddress()) != null){
        hostSet.addAll(l.getAllHosts());
      }
    }
    hostSet.remove(host);
    return hostSet;
  }

  // список других хостов, которые доступны данному хосту
  // в сети опосредованно, через другие хосты
  public Collection<Host> getAvailableHosts(@NonNull Host host){
    // Сначала найдём все связанные хосты
    Set<Host> foundHost = new HashSet<>();
    foundHost.add(host);
    flooding(host, foundHost);

    // после удаляем из сета сам хост и связанные с ним напрямую
    Set<Host> hostsLinkedDirectly = (Set<Host>) getHostsLinkedDirectly(host);
    foundHost.removeAll(hostsLinkedDirectly);
    foundHost.remove(host);
    return foundHost;
  }

  private void flooding(@NonNull Host host, Set<Host> foundHosts){
    Set<Host> hostsLinkedDirectly = (Set<Host>) getHostsLinkedDirectly(host);
    hostsLinkedDirectly.removeAll(foundHosts);
    foundHosts.addAll(hostsLinkedDirectly);

    for (Host h: hostsLinkedDirectly) {
      flooding(h, foundHosts);
    }
  }

  // недостижимые хосты
  public Collection<Host> getUnreachableHosts(){
    Set<Host> linkedHosts = new HashSet<>();
    for (CommunicationLine communicationLine: getAllCommunicationLines()) {
      linkedHosts.addAll(communicationLine.getAllHosts());
    }
    Set<Host> allHosts = new HashSet<>(getAllHosts());
    allHosts.removeAll(linkedHosts);
    return allHosts;
  }

  public Host getHost(@NonNull IpAddress ipAddress) {
    return hosts.stream()
            .filter(e -> e.getIpAddress().equals(ipAddress))
            .findFirst()
            .orElse(null);
  }

  public CommunicationLine getCommunicationLine(@NonNull String lineName, @NonNull LineType type) {
    return communicationLines.stream()
            .filter(e -> e.getLineName().equals(lineName) && e.getType().equals(type))
            .findFirst()
            .orElse(null);
  }

  public Collection<Host> getAllHosts() {
    return hosts.stream().toList();
  }

  public Collection<CommunicationLine> getAllCommunicationLines() {
    return communicationLines.stream().toList();
  }
}
