package vpn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vpn.model.User;
import vpn.model.VpnHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeSet;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id FROM User u WHERE u.userInitials = :initials")
    public Long findIdByUserInitials(@Param("initials") String initials);
}
