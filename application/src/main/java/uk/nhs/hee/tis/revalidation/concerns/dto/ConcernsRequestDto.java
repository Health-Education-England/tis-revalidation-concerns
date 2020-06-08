package uk.nhs.hee.tis.revalidation.concerns.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConcernsRequestDto {
  private String sortColumn;
  private String sortOrder;
  private boolean concernsStatusClosed;
  private int pageNumber;
  private String searchQuery;
}
