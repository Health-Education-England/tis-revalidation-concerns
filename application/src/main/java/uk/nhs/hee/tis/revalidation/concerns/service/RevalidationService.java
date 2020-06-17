package uk.nhs.hee.tis.revalidation.concerns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;

import static java.lang.String.format;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@Transactional
public class RevalidationService {

  private static final String DOCTORS_URL = "v1/doctors";

  @Value("${app.concern.revalidation.url}")
  private String revalidationUrl;

  @Autowired
  private RestTemplate restTemplate;

  public TraineeSummaryDto getTraineeInfo(final ConcernsRequestDto requestDto) {
    final var uriComponentsBuilder = buildTraineeRequestUrl(requestDto);

    final var responseEntity = restTemplate.exchange(uriComponentsBuilder.toUriString(),
        GET, EMPTY, TraineeSummaryDto.class);

    if (OK == responseEntity.getStatusCode()) {
      return responseEntity.getBody();
    }
    throw new RuntimeException("Failed to fetched trainee information: " + responseEntity.getStatusCode());
  }

  private UriComponentsBuilder buildTraineeRequestUrl(final ConcernsRequestDto requestDto) {
    return UriComponentsBuilder.fromUriString(format("%s/%s", revalidationUrl, DOCTORS_URL))
        .queryParam("sortColumn", requestDto.getSortColumn())
        .queryParam("pageNumber", requestDto.getPageNumber())
        .queryParam("sortOrder", requestDto.getSortOrder());
  }
}
