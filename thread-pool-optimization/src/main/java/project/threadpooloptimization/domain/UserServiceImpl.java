package project.threadpooloptimization.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResult> findAllUser(){
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResult::of).toList();
    }
}
