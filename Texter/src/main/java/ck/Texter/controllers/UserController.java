package ck.Texter.controllers;

import ck.Texter.dao.messageDao;
import ck.Texter.dao.userDao;
import ck.Texter.entity.Contact;
import ck.Texter.entity.User;
import ck.Texter.entity.Message;
import java.security.Principal;
import java.util.List;
import ck.Texter.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    @Autowired
    private userDao userDao;
    @Autowired
    private messageDao messageDao;
    @Autowired
    private UserService userService;

    //metody dotyczaca rejestracji i logowania
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model m) {
        m.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerPagePOST(@ModelAttribute(value = "user") User user, BindingResult binding) {
        if (binding.hasErrors()) { return "register"; }
        userService.registerUser(user);
       return "redirect:/login";
    }
    //metody dotyczace profilu uzytkownika
    @GetMapping("/homePage")
    public String profilePage(Model m, Principal principal) {
        m.addAttribute("user", userDao.findByLogin(principal.getName()));
        return "homePage";
    }

    @GetMapping("/confirmDelete")
    public String confirmDeletePage(Model m, Principal principal) {
        m.addAttribute("user", userDao.findByLogin(principal.getName()));
        return "confirmDelete";
    }

    @RequestMapping("/delete")
    public String deleteUserPage(@ModelAttribute(value = "user") User user){
        userService.deleteUser(user);
        return "redirect:/logout";
    }
    
    @GetMapping("/edit")
    public String editUserPage(Model m, Principal principal){
        m.addAttribute("user", userDao.findByLogin(principal.getName()));
        return "edit";
    }
    @PostMapping("/edit")
    public String editPagePOST(@ModelAttribute(value = "user") User user) {
        userService.editUser(user);
        return "redirect:/homePage";
    }

    //metody dotyczace kontaktow
    @GetMapping("/users")
    public String usersPage(Model m, Principal principal){
        List<User> listUsers = userService.createPossibleContactList(userDao.findByLogin(principal.getName()));
        m.addAttribute("usersList", listUsers);
        return "allUsers";
    }

    @GetMapping("/addContact/{contactId}")
    public String addContactPost(@PathVariable Integer contactId, Principal principal) {
        userService.addContact(userDao.findById(contactId).orElse(null), userDao.findByLogin(principal.getName()));
        return "redirect:/users";
    }

    @GetMapping("/contacts")
    public String contactsPage(Model m, Principal principal){
        List<Contact> listContacts = userService.createContactList(principal.getName());
        m.addAttribute("contactsList", listContacts);
        return "contacts";
    }

    @RequestMapping("/deleteContact/{contactId}")
    public String deleteContactPage(@PathVariable Integer contactId, Principal principal){
        userService.deleteContact(contactId);
        return "redirect:/contacts";
    }

    //metody dotyczace wiadomosci

    @GetMapping("/conversation/{senderId}")
    public String conversationPage(@PathVariable Integer senderId, Model m, Principal principal) {
        if (senderId != null) {
            List<Message> messageList = userService.prepareMessageList(principal.getName(), senderId);
            m.addAttribute("messagesList", messageList);
            Message newMessage = userService.prepareMessage(principal.getName(), senderId);
            Integer loggedUserId = userDao.findByLogin(principal.getName()).getUserId();
            if (newMessage != null) {
                m.addAttribute("loggedUserId", loggedUserId);
                m.addAttribute("newMessage", newMessage);
                return "conversation";
            } else return "error";
        }   else return "error";
    }

    @PostMapping("/conversation")
    public String conversationPost(@ModelAttribute(value = "message") Message newMessage) {
        userService.createMessage(newMessage);
        return "redirect:/conversation/"+newMessage.getReceiver().getUserId();
    }
    
}
