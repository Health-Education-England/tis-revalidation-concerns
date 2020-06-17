package uk.nhs.hee.tis.revalidation.concerns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcernsSummaryDto {

  private long countTotal;
  private long totalPages;
  private long totalResults;
  private List<ConcernTraineeDto> concernTrainees;
}
