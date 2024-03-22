package ru.teamscore.java23.network.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.network.model.entities.Host;
import ru.teamscore.java23.network.model.entities.IpAddress;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class HostManager {

  private final EntityManager entityManager;

  public long getHostsCount() {
    return entityManager
            .createNamedQuery("hostsCount", Long.class)
            .getSingleResult();
  }

  public Host[] getHostsAll() {
    return entityManager
            .createQuery("from Host order by id", Host.class)
            .getResultList()
            .toArray(Host[]::new);
  }

  public Optional<Host> getHost(@NonNull IpAddress ipAddress) {
    try {
      return Optional.of(entityManager
              .createNamedQuery("hostByIpAddress", Host.class)
              .setParameter("ipAddress", ipAddress)
              .getSingleResult());
    } catch (NoResultException | NullPointerException e) {
      return Optional.empty();
    }
  }

  public Optional<Host> getHost(@NonNull String ipAddress) {
    return getHost(new IpAddress(ipAddress));
  }

  public Optional<Host> getHost(long id) {
    try {
      return Optional.of(entityManager
              .find(Host.class, id));
    } catch (NoResultException | NullPointerException e) {
      return Optional.empty();
    }
  }

  public Optional<Host> getHost(@NonNull Host host) {
    if (entityManager.contains(host)) {
      return Optional.of(host);
    }
    return getHost(host.getId());
  }

  public void addHost(@NonNull Host host) {
    var transaction = entityManager.getTransaction();
    transaction.begin();
    try {
      entityManager.persist(host);
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    }
  }

  public void updateHost(@NonNull Host host) {
    entityManager.getTransaction().begin();
    entityManager.merge(host);
    entityManager.getTransaction().commit();
  }

  public void removeHost(@NonNull Host host){
    entityManager.getTransaction().begin();
    entityManager.remove(host);
    entityManager.getTransaction().commit();
  }

  public Collection<Host> find(String search) {
    String pattern = "%" + search + "%";
    return entityManager
            .createQuery("from Host where macAddress ilike :pattern or " +
                            "cast(ipAddress as String) ilike :pattern",
                    Host.class)
            .setParameter("pattern", pattern)
            .getResultList();
  }
}
