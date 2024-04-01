package vpn.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vpn.model.User;
import vpn.model.WorkFromHome;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkFromHomeRepository extends JpaRepository<WorkFromHome, Long> {
    @Transactional
    public List<WorkFromHome> findByUserAndDateWfh(User user, LocalDate dateWfh);
}
