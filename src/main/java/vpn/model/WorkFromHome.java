package vpn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "work_from_home", schema = "vpn")
@Access(AccessType.FIELD)
public class WorkFromHome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_wfh")
    private LocalDate dateWfh;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkFromHome that = (WorkFromHome) o;

        // Check if user is not null before invoking equals
        if (user == null || that.user == null) {
            return false;
        }

        return dateWfh.equals(that.dateWfh) && user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateWfh, user);
    }
}


