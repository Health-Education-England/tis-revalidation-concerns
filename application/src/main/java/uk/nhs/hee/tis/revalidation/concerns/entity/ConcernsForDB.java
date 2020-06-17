package uk.nhs.hee.tis.revalidation.concerns.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "concernsForDB")
public class ConcernsForDB {
  @Id
  private String gmcReferenceNumber;
  private String doctorFirstName;
  private String doctorLastName;
  private String programme;
  private String concernsStatus;

  private LocalDate dateRaised;
  private String type;
  private String site;
  private String source;
  private String status;
  private String admin;
  private LocalDate followUpDate;
  private LocalDate closedDate;
  private LocalDate dateAdded;

}
