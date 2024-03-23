package ru.teamscore.java23.network.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "line_host", schema = "communication_lines")
public class LineHost {

  @Embeddable
  static class LineHostPK {
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "line_id")
    private CommunicationLine communicationLine;
  }

  @EmbeddedId
  private final LineHostPK pk = new LineHostPK();

  @Transient
  public Host getHost() {
    return pk.getHost();
  }

  @Transient
  public CommunicationLine getCommunicationLine() {
    return pk.getCommunicationLine();
  }

  public LineHost(@NonNull Host host, @NonNull CommunicationLine communicationLine) {
    pk.host = host;
    pk.communicationLine = communicationLine;
  }

}
