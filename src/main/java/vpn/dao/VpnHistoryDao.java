package vpn.dao;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vpn.model.User;
import vpn.model.VpnHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Component
public class VpnHistoryDao {
    @Autowired
    VpnHistoryRepository vpnHistoryRepository;

    public Map<User, Set<VpnHistory>> findAllUsersWhoConnectedFromVpn(LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.plusDays(1).atStartOfDay();
        Map<User, Set<VpnHistory>> userToVpnHistories = new TreeMap<>();
        Set<VpnHistory> vpnHistories = vpnHistoryRepository.findAllByVpnHistoriesStartDateOrVpnHistoriesEndDate(startDate, endDate);
        for (VpnHistory vpnHistory : vpnHistories) {
            User user = vpnHistory.getUser();
            userToVpnHistories.putIfAbsent(user, new TreeSet<>());
            if(vpnHistory.getStartDate()!=null && vpnHistory.getStartDate().isBefore(startDate)) {
                vpnHistory.setStartDate(startDate);
            }
            if(vpnHistory.getEndDate()!=null && vpnHistory.getEndDate().isAfter(endDate)) {
                vpnHistory.setEndDate(endDate);
            }
            if (vpnHistory.getEndDate() == null) {
                vpnHistory.setEndDate(LocalDateTime.now());
            }
            userToVpnHistories.get(user).add(vpnHistory);
        }
        return userToVpnHistories;
    }

    public Set<VpnHistory> getActiveUsersInVpn() {
        return vpnHistoryRepository.findAllByEndDateIsNull();
    }

    public void insertVpnHistoryIfNotExists(VpnHistory vpnHistory) {
        vpnHistoryRepository.insertVpnHistoryIfNotExists(vpnHistory.getStartDate(), vpnHistory.getEndDate(), vpnHistory.getUser().getId());
    }

    //    @Transactional
    public void insertEndDateVpnHistory(VpnHistory vpnHistory) {
        vpnHistoryRepository.save(vpnHistory);
    }

    public void deleteAll() {
        vpnHistoryRepository.deleteAllInBatch();
    }
}
