package za.ac.cput.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.AdminInviteCode;

import java.util.Optional;

public interface AdminInviteCodeRepository extends JpaRepository<AdminInviteCode, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select code from AdminInviteCode code where code.codeHash = :hash")
    Optional<AdminInviteCode> findByHashForUpdate(@Param("hash") String hash);
}
