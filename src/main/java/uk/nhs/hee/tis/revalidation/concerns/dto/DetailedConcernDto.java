package uk.nhs.hee.tis.revalidation.concerns.dto;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Detailed Concerns of a Trainee")
public class DetailedConcernDto {

  private String gmcNumber;
  private List<ConcernsRecordDto> concerns;
}
