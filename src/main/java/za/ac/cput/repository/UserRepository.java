package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("select count(u) from User u")
    long countAllUsers();

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByStaffNumber(String staffNumber);

    @Query("select u from User u where (:role is null or u.role = :role) " +
            "and (:search is null or lower(u.name) like concat('%', lower(:search), '%') " +
            "or lower(u.email) like concat('%', lower(:search), '%'))")
    Page<User> findByRoleAndSearch(@Param("role") RoleEnum role,
                                   @Param("search") String search,
                                   Pageable pageable);
}
