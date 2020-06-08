package uk.nhs.hee.tis.revalidation.concerns.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@ApiModel(description = "List of trainee doctors with total count")
public class ConcernsSummaryDto {
  private long countTotal;
}
