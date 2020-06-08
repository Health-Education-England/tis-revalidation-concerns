package uk.nhs.hee.tis.revalidation.concerns.entity;

import static java.time.LocalDate.now;

import java.time.LocalDate;
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
@Document(collection = "concernsForDB")
public class ConcernsForDB {
  @Id
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
  private LocalDate followupDate;
  private LocalDate closedDate;
  private LocalDate dateAdded;

/*  //private UnderNotice underNotice;
  private String sanction;
  private RecommendationStatus doctorStatus;
  private LocalDate lastUpdatedDate;
  private String designatedBodyCode;*/

  /*public final static DoctorsForDB convert(final DoctorsForDbDto doctorsForDBDTO) {
    return DoctorsForDB.builder()
        .gmcReferenceNumber(doctorsForDBDTO.getGmcReferenceNumber())
        .doctorFirstName(doctorsForDBDTO.getDoctorFirstName())
        .doctorLastName(doctorsForDBDTO.getDoctorLastName())
        .submissionDate(doctorsForDBDTO.getSubmissionDate())
        .dateAdded(doctorsForDBDTO.getDateAdded())
        .underNotice(UnderNotice.fromString(doctorsForDBDTO.getUnderNotice()))
        .sanction(doctorsForDBDTO.getSanction())
        .doctorStatus(NOT_STARTED)
        .designatedBodyCode(doctorsForDBDTO.getDesignatedBodyCode())
        .lastUpdatedDate(now())
        .build();
  }*/
}
