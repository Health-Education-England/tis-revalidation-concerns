package uk.nhs.hee.tis.revalidation.concerns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;

import static java.lang.String.format;

@Slf4j
@Service
@Transactional
public class RevalidationService {

  @Value("${app.concern.revalidation.url}")
  private String revalidationUrl;

  @Autowired
  private RestTemplate restTemplate;

  public TraineeSummaryDto getTraineeInfo(final ConcernsRequestDto requestDto) {
    final var uriComponentsBuilder = buildTraineeRequestUrl(requestDto);

    final var responseEntity = restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET, HttpEntity.EMPTY, TraineeSummaryDto.class);

    if (HttpStatus.OK == responseEntity.getStatusCode()) {
      final var traineeSummary = responseEntity.getBody();
      return traineeSummary;
    }
    throw new RuntimeException("Failed to fetched trainee information: " + responseEntity.getStatusCode());
  }

  private UriComponentsBuilder buildTraineeRequestUrl(final ConcernsRequestDto requestDto) {
    String doctorsUrl = "v1/doctors";
    return UriComponentsBuilder.fromUriString(format("%s/%s", revalidationUrl, doctorsUrl))
        .queryParam("sortColumn", requestDto.getSortColumn())
        .queryParam("pageNumber", requestDto.getPageNumber())
        .queryParam("sortOrder", requestDto.getSortOrder());
  }
}
