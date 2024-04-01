package vpn.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vpn.model.VpnHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Repository
public interface VpnHistoryRepository extends JpaRepository<VpnHistory, Long> {

    //if end date is null that means user logged in from VPN at the moment
    public Set<VpnHistory> findAllByEndDateIsNull();

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO vpn_history (start_date, end_date, user_id) " +
            "SELECT :startDate, :endDate, :userId " +
            "FROM DUAL " +
            "WHERE NOT EXISTS (SELECT 1 FROM vpn_history WHERE user_id = :userId AND start_date IS NOT NULL AND end_date IS NULL)",
            nativeQuery = true)
    void insertVpnHistoryIfNotExists(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("userId") Long userId);

    @Query("SELECT v FROM VpnHistory v WHERE v.startDate BETWEEN :startDate AND :endDate OR v.endDate BETWEEN :startDate AND :endDate")
    public TreeSet<VpnHistory> findAllByVpnHistoriesStartDateOrVpnHistoriesEndDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
