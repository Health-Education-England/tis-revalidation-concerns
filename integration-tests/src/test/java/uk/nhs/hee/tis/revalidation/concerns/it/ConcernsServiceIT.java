package uk.nhs.hee.tis.revalidation.concerns.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.concerns.ConcernsApplication;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.service.ConcernsService;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConcernsApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class ConcernsServiceIT extends BaseIT {

  @Autowired
  private ConcernsService concernsService;

  @Before
  public void setup() {
    setupData();
  }

  @DisplayName("Trainee concerns summary, should be sorted by dateRaised column")
  @Test
  public void shouldReturnConcernSummaryForTrainees() throws Exception {
    stubCoreRequest(traineeSummaryDto);

    final var requestDTO = ConcernsRequestDto.builder()
        .sortColumn("dateRaised")
        .sortOrder("desc")
        .searchQuery("")
        .build();

    final var concernsSummaryDto = concernsService.getConcernsSummary(requestDTO);
    assertThat(concernsSummaryDto.getCountTotal(), is(1L));
    final var concernTraineeDto = concernsSummaryDto.getConcernTrainees().get(0);

    assertThat(concernTraineeDto.getGmcReferenceNumber(), is(gmcReferenceNumber));
    assertThat(concernTraineeDto.getDoctorFirstName(), is(doctorFirstName));
    assertThat(concernTraineeDto.getDoctorLastName(), is(doctorLastName));
    assertThat(concernTraineeDto.getProgramme(), is(programme));
    assertThat(concernTraineeDto.getConcernsStatus(), is(concernsStatus));
    assertThat(concernTraineeDto.getDateRaised(), is(dateRaised));
    assertThat(concernTraineeDto.getType(), is(type));
    assertThat(concernTraineeDto.getSite(), is(site));
    assertThat(concernTraineeDto.getSource(), is(source));
    assertThat(concernTraineeDto.getStatus(), is(status));
    assertThat(concernTraineeDto.getAdmin(), is(admin));
    assertThat(concernTraineeDto.getFollowUpDate(), is(followUpDate));
    assertThat(concernTraineeDto.getClosedDate(), is(closedDate));
    assertThat(concernTraineeDto.getDateAdded(), is(dateAdded));

  }
}
