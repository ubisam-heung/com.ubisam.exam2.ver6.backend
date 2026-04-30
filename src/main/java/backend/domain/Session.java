package backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "example_session")
@NoArgsConstructor
@AllArgsConstructor
public class Session {

  @Id
  private String principal;

  private String state;

  private Long timestamp;
  
}
