package project.threadpooloptimization.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.threadpooloptimization.domain.User;
import project.threadpooloptimization.domain.UserRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;

    @Override
    public User save(User user) {
        return userJPARepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userJPARepository.findAll();
    }
}
