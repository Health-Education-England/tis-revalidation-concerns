package uk.nhs.hee.tis.revalidation.concerns.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reference {

  private long id;
  private String label;

}
