package project.springmybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.springmybatis.domain.User;

import java.util.List;

@Mapper
public interface UserMapper {
    
    void insertUser(User user);
    
    User selectUserById(Long id);
    
    List<User> selectAllUsers();
    
    void updateUser(User user);
    
    void deleteUser(Long id);
    
    List<User> selectUsersByName(String name);
}