package uk.nhs.hee.tis.revalidation.concerns.dto;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Current and legacy Concern information")
public class ConcernsDto {

  private String concernId;
  private String gmcNumber;
  private LocalDate dateOfIncident;
  private ReferenceDto concernType;
  private ReferenceDto source;
  private LocalDate dateReported;
  private String employer;
  private ReferenceDto site;
  private ReferenceDto grade;
  private ReferenceDto status;
  private String admin;
  private LocalDate followUpDate;
  private LocalDate lastUpdatedDate;
  private List<String> comments;
}
