package uk.nhs.hee.tis.revalidation.concerns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcernTraineeDto {

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
