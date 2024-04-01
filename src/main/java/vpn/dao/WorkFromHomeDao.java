package vpn.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vpn.model.User;
import vpn.model.WorkFromHome;

import java.time.LocalDate;
import java.util.List;

@Component
public class WorkFromHomeDao {
    @Autowired
    private  WorkFromHomeRepository workFromHomeRepository;

    public  void insertWorkFromHome(WorkFromHome workFromHome){
        List<WorkFromHome> workFromHomes =  takeWfhId(workFromHome.getUser(), workFromHome.getDateWfh());
        if (workFromHomes != null && !workFromHomes.isEmpty()) {
            workFromHome.setId(workFromHomes.get(0).getId());
        } else {
            workFromHomeRepository.save(workFromHome);
        }
    }

    public List<WorkFromHome> takeWfhId(User user, LocalDate date) {
        return workFromHomeRepository.findByUserAndDateWfh(user, date);
    }

    public void deleteAll() {
        workFromHomeRepository.deleteAllInBatch();
    }
}
