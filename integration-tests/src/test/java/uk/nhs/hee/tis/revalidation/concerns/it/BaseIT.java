package uk.nhs.hee.tis.revalidation.concerns.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.time.LocalDate.now;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;

public class BaseIT {
  protected final Faker faker = new Faker();

  @Rule
  public WireMockRule wireMockRule = new WireMockRule();

  @Autowired
  public ObjectMapper objectMapper;

  protected TraineeSummaryDto traineeSummaryDto;
  protected long countTotal;
  protected long countUnderNotice;
  protected long totalPages;
  protected long totalResults;

  protected TraineeInfoDto traineeInfoDto;
  protected String gmcReferenceNumber;
  protected String doctorFirstName;
  protected String doctorLastName;
  protected LocalDate submissionDate;
  protected LocalDate dateAdded;
  protected String underNotice;
  protected String sanction;
  protected String doctorStatus;
  protected String programme;
  protected String concernsStatus;
  protected String programmeMembershipType;
  protected LocalDate cctDate;
  protected String currentGrade;
  protected String admin;
  protected LocalDate lastUpdatedDate;
  protected LocalDate dateRaised;
  protected String type;
  protected String site;
  protected String source;
  protected String status;
  protected LocalDate followUpDate;
  protected LocalDate closedDate;

  protected List<TraineeInfoDto> traineeInfoList;

  protected void setupData() {
   countUnderNotice = faker.number().randomNumber();
   totalPages = faker.number().randomNumber();
   totalResults = faker.number().randomNumber();
   traineeInfoList = new ArrayList<>();

    gmcReferenceNumber = faker.number().digits(7);
    doctorFirstName = faker.name().firstName();
    doctorLastName = faker.name().lastName();
    submissionDate = now().minusDays(10);
    dateAdded = now().minusDays(11);
    underNotice = "Yes";
    sanction = faker.lorem().characters(2);;
    doctorStatus = "Under Notice";
    programme = "General Medicine";
    programmeMembershipType = "SUBSTANTIVE";
    cctDate = now().plusMonths(10);
    currentGrade = "Academic Clinical Lecturer";
    admin = "admin@admin.com";
    lastUpdatedDate = now().minusDays(5);
    concernsStatus = "Open";
    dateRaised = now();
    type = "concern";
    site = "site";
    source = "source";
    status = "Under Notice";
    followUpDate = now();
    closedDate = now();

    traineeInfoDto = new TraineeInfoDto(gmcReferenceNumber, doctorFirstName, doctorLastName, submissionDate,
        dateAdded, underNotice, sanction, doctorStatus, programme, programmeMembershipType, cctDate, currentGrade, admin, lastUpdatedDate);
    traineeInfoList.add(traineeInfoDto);

   traineeSummaryDto = new TraineeSummaryDto(traineeInfoList.size(), countUnderNotice, totalPages, totalResults, traineeInfoList);
  }

  public void stubCoreRequest(final TraineeSummaryDto revalData) throws JsonProcessingException {
    stubFor(get(urlPathMatching("/revalidation/api/v1/doctors"))
        .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(objectMapper.writeValueAsBytes(revalData))));
  }

  public void stubCoreRequestReturn400() throws JsonProcessingException {
    stubFor(get(urlPathMatching("/revalidation/api/v1/doctors/.*"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", "application/json")));
  }

  public void stubCoreRequestReturn404() throws JsonProcessingException {
    stubFor(get(urlPathMatching("/revalidation/api/v1/doctors/.*"))
        .willReturn(aResponse()
            .withStatus(404)
            .withHeader("Content-Type", "application/json")));
  }

  public void stubCoreRequestReturn500() throws JsonProcessingException {
    stubFor(get(urlPathMatching("/revalidation/api/v1/doctors/.*"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader("Content-Type", "application/json")));
  }

}
