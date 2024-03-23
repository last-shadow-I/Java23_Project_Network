package ru.teamscore.java23.network.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.network.model.entities.CommunicationLine;
import ru.teamscore.java23.network.model.entities.Network;

import java.util.Optional;

@RequiredArgsConstructor
public class NetworkManager {

  private final EntityManager entityManager;

  public Network getNetwork() {
    return entityManager
            .createQuery("from Network order by id", Network.class)
            .getSingleResult();
  }

  public void updateNetwork(@NonNull Network network) {
    entityManager.getTransaction().begin();
    entityManager.merge(network);
    entityManager.getTransaction().commit();
  }

  public long getLinesCount() {
    return entityManager
            .createQuery("select count(*) from CommunicationLine", Long.class)
            .getSingleResult();
  }

  public CommunicationLine[] getLinesAll() {
    return entityManager
            .createQuery("from CommunicationLine order by id", CommunicationLine.class)
            .getResultList()
            .toArray(CommunicationLine[]::new);
  }

  public Optional<CommunicationLine> getLine(long id) {
    try {
      return Optional.of(entityManager
              .find(CommunicationLine.class, id));
    } catch (NoResultException | NullPointerException e) {
      return Optional.empty();
    }
  }

  public void addLine(CommunicationLine communicationLine) {
    var transaction = entityManager.getTransaction();
    transaction.begin();
    try {
      entityManager.persist(communicationLine);
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    }
  }

  public void updateLine(@NonNull CommunicationLine communicationLine) {
    entityManager.getTransaction().begin();
    entityManager.merge(communicationLine);
    entityManager.getTransaction().commit();
  }

  public void removeLine(@NonNull CommunicationLine communicationLine) {
    entityManager.getTransaction().begin();
    entityManager.remove(communicationLine);
    entityManager.getTransaction().commit();
  }


}
