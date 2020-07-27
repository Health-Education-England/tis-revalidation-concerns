package uk.nhs.hee.tis.revalidation.concerns.entity;

import java.time.LocalDate;
import java.util.List;
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
@Document(collection = "concerns")
public class Concern {

  @Id
  private String id;
  private String gmcNumber;
  private LocalDate dateOfIncident;
  private Reference concernType;
  private Reference source;
  private LocalDate dateReported;
  private String employer;
  private Reference site;
  private Reference grade;
  private Reference status;
  private String admin;
  private LocalDate followUpDate;
  private LocalDate lastUpdatedDate;
  private List<String> comments;
  private LocalDate closedDate;
  private LocalDate dateAdded;

}
