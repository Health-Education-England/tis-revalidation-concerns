package uk.nhs.hee.tis.revalidation.concerns.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;

@Repository
public interface ConcernsRepository extends MongoRepository<Concern, String> {

  List<Concern> findAllByGmcNumber(final String gmcNumber);

}
