package uk.nhs.hee.tis.revalidation.concerns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferenceDto {

  private long id;
  private String label;

}
