package ck.Texter.dao;

import ck.Texter.entity.User;
import ck.Texter.entity.Message;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface messageDao extends CrudRepository<Message, Integer>{

    public List<Message> findByReceiverAndSender(User receiver, User sender);
    public List<Message> findBySenderAndReceiver(User sender, User receiver);

    public List<Message> findByReceiver(User receiver);
    public List<Message> findBySender(User sender);
    
}


