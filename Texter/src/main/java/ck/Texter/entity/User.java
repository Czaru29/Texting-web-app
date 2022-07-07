package ck.Texter.entity;

import lombok.*;

import java.util.Set;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(unique=true)
    private String login;
    private String password;

    @OneToMany(mappedBy="sender")
    private Set<Message> messagesSend;
    @OneToMany(mappedBy="receiver")
    private Set<Message> messagesReceived;

    @OneToMany(mappedBy="owner")
    private Set<Contact> isOwnerOfContact;
    @OneToMany(mappedBy="contact")
    private Set<Contact> isContacts;


}
