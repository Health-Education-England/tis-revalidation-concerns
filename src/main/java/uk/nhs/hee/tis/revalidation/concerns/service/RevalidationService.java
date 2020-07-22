package uk.nhs.hee.tis.revalidation.concerns.service;

import static java.lang.String.format;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.exception.RevalidationException;

@Slf4j
@Service
public class RevalidationService {

  protected static final String DOCTORS_URL = "v1/doctors";

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
    throw new RevalidationException(
        "Failed to fetched trainee information: " + responseEntity.getStatusCode());
  }

  private UriComponentsBuilder buildTraineeRequestUrl(final ConcernsRequestDto requestDto) {
    return UriComponentsBuilder.fromUriString(format("%s/%s", revalidationUrl, DOCTORS_URL))
        .queryParam("sortColumn", requestDto.getSortColumn())
        .queryParam("sortOrder", requestDto.getSortOrder())
        .queryParam("searchQuery", requestDto.getSearchQuery())
        .queryParam("pageNumber", requestDto.getPageNumber());
  }
}
