package ck.Texter.dao;
import ck.Texter.entity.User;
import ck.Texter.entity.Contact;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface userDao extends CrudRepository<User, Integer> {
    
    public User findByLogin(String login);
    public boolean existsByLogin(String login);

}




