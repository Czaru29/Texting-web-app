package ck.Texter.services;

import ck.Texter.dao.contactDao;
import ck.Texter.dao.messageDao;
import ck.Texter.dao.userDao;
import ck.Texter.entity.Contact;
import ck.Texter.entity.Message;
import ck.Texter.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private userDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private messageDao messageDao;
    @Autowired
    private contactDao contactDao;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    public void deleteUser(User user) {
        if(userDao.existsByLogin(user.getLogin())) {
            List<Message> messageList = messageDao.findByReceiver(userDao.findByLogin(user.getLogin()));
            messageList.addAll(messageDao.findBySender(userDao.findByLogin(user.getLogin())));
            messageDao.deleteAll(messageList);
            userDao.delete(userDao.findByLogin(user.getLogin()));
        }
    }

    public void editUser(User user) {
        if(userDao.existsByLogin(user.getLogin())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(user);
        }
    }

    public List<User> createPossibleContactList(User loggedUser) {
        List<User> listUsers = (List<User>) userDao.findAll();
        listUsers.remove(loggedUser);
        listUsers.removeAll(loggedUser.getIsOwnerOfContact().stream().map(Contact::getContact).collect(Collectors.toList()));
        return listUsers;
    }

    public void addContact(User newContact, User loggedUser) {
        if(userDao.existsById(newContact.getUserId()) && userDao.existsById(loggedUser.getUserId())) {
            Contact contact = new Contact();
            contact.setOwner(loggedUser);
            contact.setContact(newContact);
            contactDao.save(contact);
        }
    }

    public List<Contact> createContactList(String login) {
        List<Contact> contacts = contactDao.findByOwner(userDao.findByLogin(login));
        return contacts;
    }

    public void deleteContact(Integer contactId) {
        if(contactDao.existsById(contactId))
            contactDao.deleteById(contactId);

    }

    public Message prepareMessage(String login, Integer readerId) {
        Message newMessage = new Message();
        newMessage.setSender(userDao.findByLogin(login));
        newMessage.setReceiver(userDao.findById(readerId).orElse(null));
        if(newMessage.getSender()!=null && newMessage.getReceiver()!=null) {
            return newMessage;
        } else return null;
    }

    public void createMessage(Message message) {
        if(message.getContent().length()<=255) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            message.setDate(currentDateTime);
            messageDao.save(message);
        }
    }

    public List<Message> prepareMessageList(String login, Integer senderId) {
        List<Message> messageList = messageDao.findByReceiverAndSender(
                userDao.findByLogin(login),
                userDao.findById(senderId).orElse(null));
        messageList.addAll(messageDao.findBySenderAndReceiver(
                userDao.findByLogin(login),
                userDao.findById(senderId).orElse(null)));
        messageList = messageList.stream().sorted(Comparator.comparing(Message::getDate)).collect(Collectors.toList());
        return messageList;
    }



}
