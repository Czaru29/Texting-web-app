package ck.Texter.dao;

import ck.Texter.entity.Contact;
import ck.Texter.entity.Message;
import ck.Texter.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface contactDao extends CrudRepository<Contact, Integer> {

    public List<Contact> findByOwner(User owner);

}




