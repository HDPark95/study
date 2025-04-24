package project.threadpooloptimization.interfaces;

import project.threadpooloptimization.domain.UserResult;

public record UserResponse(
        Long id,
        String name,
        String phoneNumber
) {
    public static UserResponse of(UserResult userResult){
        return new UserResponse(userResult.id(), userResult.name(), userResult.phoneNumber());
    }
}
