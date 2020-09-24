package uk.nhs.hee.tis.revalidation.concerns.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Current and legacy Concern information")
public class ConcernsRecordDto {

  private String concernsStatus;
  private LocalDate dateRaised;
  private String type;
  private String site;
  private String source;
  private LocalDate followUpDate;
  private LocalDate closedDate;
}
