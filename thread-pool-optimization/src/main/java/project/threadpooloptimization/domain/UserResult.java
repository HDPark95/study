package project.threadpooloptimization.domain;

public record UserResult(
        Long id,
        String name,
        String phoneNumber
) {
    public static UserResult of(User user){
        return new UserResult(user.getId(), user.getName(), user.getPhoneNumber());
    }
}
