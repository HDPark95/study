package project.threadpooloptimization.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.threadpooloptimization.domain.UserResult;
import project.threadpooloptimization.domain.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(){
        List<UserResult> users = userService.findAllUser();
        return ResponseEntity.ok().body(
                users.stream().map(UserResponse::of).toList()
        );
    }
}
