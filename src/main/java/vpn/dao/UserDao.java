package vpn.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vpn.model.User;
import vpn.model.VpnHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class UserDao {
    @Autowired
    private UserRepository userRepository;

    public void insertUser(User user) {
        Long id = takeUserId(user.getUserInitials());

        if (id != null) {
            user.setId(id);
        } else {
            userRepository.save(user);
        }
    }

    public Long takeUserId(String userInitials) {
        return userRepository.findIdByUserInitials(userInitials);
    }


    public void deleteAll() {
        userRepository.deleteAllInBatch();
    }
}
