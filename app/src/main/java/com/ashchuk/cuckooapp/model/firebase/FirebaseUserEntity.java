package com.ashchuk.cuckooapp.model.firebase;

import com.ashchuk.cuckooapp.model.entities.Message;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.TodoItem;

import java.util.List;

public class FirebaseUserEntity {
    public String UserGuid;
    public String DisplayName;
    public String Email;
    public Integer Status;
    public List<TodoItem> Todos;
    public List<Subscription> Subscriptions;
    public List<Message> Messages;
}