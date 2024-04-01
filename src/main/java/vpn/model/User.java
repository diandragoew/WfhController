package vpn.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "users", schema = "vpn")
@Access(AccessType.FIELD)
public class User implements Comparable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_initials",unique = true, nullable = false)
    private String userInitials;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    Set<VpnHistory> vpnHistories = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    Set<WorkFromHome> workFromHome = new HashSet<>();

    public void setInitials(String userInitials) {
        this.userInitials = userInitials.toLowerCase();
    }

    public void addVpnHistory(VpnHistory vpnHistory) {
        this.vpnHistories.add(vpnHistory);
        vpnHistory.setUser(this);
    }

    public void addWorkFromHome(WorkFromHome workFromHome) {
        workFromHome.setUser(this);
        this.workFromHome.add(workFromHome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userInitials.equalsIgnoreCase(user.userInitials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInitials);
    }

    @Override
    public int compareTo(User o) {
        int result = this.userInitials.compareToIgnoreCase(o.userInitials);
        return result;
    }


}
