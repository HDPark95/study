package project.threadpooloptimization.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import project.threadpooloptimization.domain.User;

public interface UserJPARepository extends JpaRepository<User, Long> {

}
