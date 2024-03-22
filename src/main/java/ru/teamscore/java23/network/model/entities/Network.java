package ru.teamscore.java23.network.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.teamscore.java23.network.model.enums.LineType;
import ru.teamscore.java23.network.model.exceptions.ExistHostException;
import ru.teamscore.java23.network.model.exceptions.IpAddressNotMatchNetwork;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "network", schema = "networks")
public class Network {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "network_id")
  private long networkId;

  @Getter
  @Setter
  @Column(name = "ip_address", nullable = false, unique = true, length = 15)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Convert(converter = IpAddressConverter.class)
  private IpAddress ipAddress;

  @Getter
  @Setter
  @Column(name = "mask", nullable = false, length = 15)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Convert(converter = IpAddressConverter.class)
  private IpAddress networkMask;

  @Getter
  @Setter
  @Column(name = "gateway", nullable = false,  length = 15)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Convert(converter = IpAddressConverter.class)
  private IpAddress gateway;

  @OneToMany(mappedBy="network", cascade = CascadeType.ALL)
  private final List<Host> hosts = new ArrayList<>();

  @OneToMany(mappedBy="network", cascade = CascadeType.ALL)
  private final List<CommunicationLine> communicationLines = new ArrayList<>();

  // по умолчанию предлагается случайный адрес, соответствующий маске сети
  public Host addHost(String macAddress){
    Host host = new Host();
    host.setIpAddress(getUnusedIpAddress());
    host.setMacAddress(macAddress);
    host.setNetwork(this);
    hosts.add(host);
    return host;
  }

  public void addHost(IpAddress ipAddress, String macAddress){
    if(isValidIpAddress(ipAddress)){
      Host existingHost = getHost(ipAddress);

      if (existingHost == null) {
        Host host = new Host();
        host.setIpAddress(ipAddress);
        host.setMacAddress(macAddress);
        host.setNetwork(this);
        hosts.add(host);
      } else {
        throw new ExistHostException("Такой хост уже существует", existingHost);
      }
    } else {
      throw new IpAddressNotMatchNetwork("Ip адрес не подходит данной сети", ipAddress);
    }
  }

  public void addHost(String ipAddress, String macAddress){
    addHost(new IpAddress(ipAddress), macAddress);
  }

  public void addHost(Host host){
    if(isValidIpAddress(host.getIpAddress())){
      Host existingHost = getHost(host);

      if (existingHost == null) {
        hosts.add(host);
      } else {
        throw new ExistHostException("Такой хост уже существует", existingHost);
      }
    } else {
      throw new IpAddressNotMatchNetwork("Ip адрес не подходит данной сети", ipAddress);
    }
  }

  public Host removeHost(@NonNull Host host){
    var existingHost = getHost(host.getIpAddress());

    if (existingHost != null) {
      hosts.remove(existingHost);
    }
    return existingHost;
  }

  public Host removeHost(@NonNull IpAddress ipAddress){
    Host existingHost = getHost(ipAddress);

    if (existingHost != null) {
      removeHost(existingHost);
    }
    return existingHost;
  }

  public Host removeHost(String ipAddress){
    return removeHost(new IpAddress(ipAddress));
  }

  public Host removeHost(long id){
    return removeHost(getHost(id));
  }

  // получить случайный адрес, соответствующий маске сети
  public IpAddress getUnusedIpAddress(){

    int startIntAddress = ipToInteger(getIpAddress().getIntIpAddress());
    int maskIntAddress = ipToInteger(networkMask.getIntIpAddress());

    int endIntAddress = startIntAddress | ~maskIntAddress; // получаем широковещательный (последний) адрес сети

    while (true){
      int randomIpAddress = startIntAddress + (int) (Math.random() * (endIntAddress-startIntAddress));
      IpAddress unusedIp = new IpAddress(integerToIp(randomIpAddress));
      if (isValidIpAddress(unusedIp)){
        return unusedIp;
      } else {
        while (++randomIpAddress <= endIntAddress){
          unusedIp = new IpAddress(integerToIp(randomIpAddress));
          if(isValidIpAddress(unusedIp)){
            return unusedIp;
          }
        }
      }
    }
  }

  // Переводим ip адрес в битовый вид
  private static int ipToInteger(int @NonNull [] intIpAddress){
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

  public CommunicationLine addCommunicationLine(String lineName, LineType type){

    CommunicationLine communicationLine = new CommunicationLine();
    communicationLine.setLineName(lineName);
    communicationLine.setType(type);
    communicationLine.setNetwork(this);

    communicationLines.add(communicationLine);

    return communicationLine;
  }

  public CommunicationLine addCommunicationLine(CommunicationLine communicationLine){
    communicationLines.add(communicationLine);
    return communicationLine;
  }

  public CommunicationLine removeCommunicationLine(@NonNull CommunicationLine communicationLine){
    CommunicationLine existingCommunicationLine = getCommunicationLine(communicationLine);

    if (existingCommunicationLine != null) {
      communicationLines.remove(existingCommunicationLine);
    }
    return  existingCommunicationLine;
  }

  public CommunicationLine removeCommunicationLine(long id){
    return removeCommunicationLine(getCommunicationLine(id));
  }

  // количество хостов
  public int getCountHosts(){
    return hosts.size();
  }

  // количество связей,
  public int getCountCommunicationLines(){
    return communicationLines.size();
  }

  // есть ли вообще такой хост в сети
  public boolean isAddressHost(@NonNull IpAddress findIp){
    var result = hosts.stream()
            .filter(e -> {
              return e.getIpAddress().equals(findIp);
            })
            .findFirst()
            .orElse(null);
    return result != null;
  }

  // список хостов, с которыми он связан напрямую
  public Collection<Host> getHostsLinkedDirectly(@NonNull Host host){
    Set<Host> hostSet = new HashSet<>();
    for (var l: communicationLines) {
      if(l.getHost(host) != null){
        hostSet.addAll(l.getAllHosts().stream().map(LineHost::getHost).collect(Collectors.toSet()));
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
      linkedHosts.addAll(communicationLine.getAllHosts().stream().map(LineHost::getHost).collect(Collectors.toSet()));
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

  public Host getHost(@NonNull String ipAddress) {
    return getHost(new IpAddress(ipAddress));
  }

  public Host getHost(@NonNull Host host) {
    return hosts.stream()
            .filter(e -> e.equals(host))
            .findFirst()
            .orElse(null);
  }

  public Host getHost(long hostId){
    return hosts.stream()
            .filter(e -> e.getId() == hostId)
            .findFirst()
            .orElse(null);
  }

  public CommunicationLine getCommunicationLine(long id) {
    return communicationLines.stream()
            .filter(e -> e.getId() == id)
            .findFirst()
            .orElse(null);
  }

  public CommunicationLine getCommunicationLine(@NonNull CommunicationLine communicationLine) {
    return communicationLines.stream()
            .filter(e -> e.equals(communicationLine))
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
