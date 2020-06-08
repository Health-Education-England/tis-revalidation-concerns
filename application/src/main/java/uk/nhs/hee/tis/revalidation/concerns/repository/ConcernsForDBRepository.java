package uk.nhs.hee.tis.revalidation.concerns.repository;

import static java.util.List.of;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.concerns.entity.ConcernsForDB;

@Repository
public interface ConcernsForDBRepository extends MongoRepository<ConcernsForDB, String> {

  //Get under notice trainee doctors as part of search results (search by first name or last name or gmc number).
  //TODO: look at the query option too
  Page<ConcernsForDB> findByDoctorFirstNameIgnoreCaseLikeAndConcernsStatusInOrDoctorLastNameIgnoreCaseLikeAndConcernsStatusInOrGmcReferenceNumberIgnoreCaseLikeAndConcernsStatusIn(final Pageable pageable,
      final String firstName,
      final String concernsStatus1,
      final String lastName,
      final String concernsStatus2,
      final String gmcNumber,
      final String concernsStatus3);


  default Page<ConcernsForDB> findAllByConcernsStatusClosedIn(final Pageable pageable, final String searchQuery, final String concernsStatus) {
    return findByDoctorFirstNameIgnoreCaseLikeAndConcernsStatusInOrDoctorLastNameIgnoreCaseLikeAndConcernsStatusInOrGmcReferenceNumberIgnoreCaseLikeAndConcernsStatusIn(pageable,
        searchQuery,
        concernsStatus,
        searchQuery,
        concernsStatus,
        searchQuery,
        concernsStatus);
  }

}
