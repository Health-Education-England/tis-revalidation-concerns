package uk.nhs.hee.tis.revalidation.concerns.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.concerns.entity.ConcernsForDB;

@Repository
public interface ConcernsForDBRepository extends MongoRepository<ConcernsForDB, String> {


  Page<ConcernsForDB> findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(final Pageable pageable,
      final String firstName,
      final String lastName,
      final String gmcNumber);

  Page<ConcernsForDB> findByDoctorFirstNameIgnoreCaseLikeAndConcernsStatusInOrDoctorLastNameIgnoreCaseLikeAndConcernsStatusInOrGmcReferenceNumberIgnoreCaseLikeAndConcernsStatusIn(final Pageable pageable,
      final String firstName,
      final String concernStatus1,
      final String lastName,
      final String concernStatus2,
      final String gmcNumber,
      final String concernStatus3);

  default Page<ConcernsForDB> findAll(final Pageable pageable, final String searchQuery) {
    return findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(pageable,
        searchQuery,
        searchQuery,
        searchQuery);
  }

  default Page<ConcernsForDB> findAllByConcernsStatusClosedIn(final Pageable pageable, final String searchQuery, final String concernStatus) {
    return findByDoctorFirstNameIgnoreCaseLikeAndConcernsStatusInOrDoctorLastNameIgnoreCaseLikeAndConcernsStatusInOrGmcReferenceNumberIgnoreCaseLikeAndConcernsStatusIn(pageable,
        searchQuery,
        concernStatus,
        searchQuery,
        concernStatus,
        searchQuery,
        concernStatus);
  }

}
