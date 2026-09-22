package lk.fintrex.drpapi.repository;

import java.util.Optional;

import lk.fintrex.drpapi.entity.CustomerEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

public interface DrpRepository
        extends Repository<CustomerEntity, Long> {

    @Query(
            value = """
                    SELECT `id`
                    FROM `drp`.`customer`
                    WHERE `nic` = :nic
                    ORDER BY `id` DESC
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Long> findCustomerIdByNic(
            @Param("nic") String nic
    );

    @Query(
            value = """
                    SELECT `id`
                    FROM `drp`.`last_request`
                    WHERE `customer` = :customerId
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Long> findLatestRequestIdByCustomer(
            @Param("customerId") Long customerId
    );

    @Query(
            value = """
                    SELECT `response`
                    FROM `drp`.`request`
                    WHERE `id` = :requestId
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<String> findResponseByRequestId(
            @Param("requestId") Long requestId
    );
}
