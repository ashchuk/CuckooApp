package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.model.entities.Message;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.TodoItem;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.MessagesRepository;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.TodoItemsRepositiry;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import io.reactivex.Observable;

public class FirebaseUserEntityCreator {
    public static Observable<FirebaseUserEntity> create(String userId) {

        return Observable.zip(UserRepository.getUserByUserId(userId),
                SubscriptionsRepository.getSubscriptionByUserId(userId),
                TodoItemsRepositiry.getTodoItemsByUserId(userId),
                MessagesRepository.getMessagesByUserId(userId),
                (user, subscriptions, todoItems, messages) -> {
                    FirebaseUserEntity entity = new FirebaseUserEntity();

                    entity.Guid = user.Guid;
                    entity.Email = user.Email;
                    entity.PhoneNumber = user.PhoneNumber;
                    entity.DisplayName = user.DisplayName;
                    entity.Status = user.Status == null ? UserStatus.HOME.getValue() : user.Status.getValue();

                    entity.Todos = todoItems;
                    entity.Messages = messages;
                    entity.Subscriptions = subscriptions;

                    return entity;
                });
    }

    public static Observable<FirebaseUserEntity> createDummy(String userId) {

        return Observable.zip(UserRepository.getUserByUserId(userId),
                SubscriptionsRepository.getSubscriptionByUserId(userId),
                TodoItemsRepositiry.getTodoItemsByUserId(userId),
                MessagesRepository.getMessagesByUserId(userId),
                (user, subscriptions, todoItems, messages) -> {
                    FirebaseUserEntity entity = new FirebaseUserEntity();

                    entity.Guid = user.Guid;
                    entity.Email = user.Email;
                    entity.PhoneNumber = "88005553535";
                    entity.DisplayName = user.DisplayName;
                    entity.Status = user.Status == null ? UserStatus.HOME.getValue() : user.Status.getValue();
                    entity.Gps = "123123;321321";
                    entity.LastUpdateDate = user.LastUpdateDate;

                    TodoItem todoItem = new TodoItem();
                    todoItem.creationDate = new Date();
                    todoItem.id = java.util.UUID.randomUUID().toString();
                    todoItem.isDone = false;
                    todoItem.message = "todo item message";
                    todoItem.userId = user.Guid;

                    Message message = new Message();
                    message.messageText = "message text";
                    message.id = java.util.UUID.randomUUID().toString();
                    message.creationDate = new Date();
                    message.creatorId = user.Guid;
                    message.userId = user.Guid;

                    Subscription subscription = new Subscription();
                    subscription.id = java.util.UUID.randomUUID().toString();
                    subscription.lastUpdateDate = new Date();
                    subscription.status = UserStatus.DRIVE.getValue();
                    subscription.userId = user.Guid;

                    entity.Todos = new ArrayList<>(Arrays.asList(todoItem,todoItem));
                    entity.Messages = new ArrayList<>(Arrays.asList(message, message));
                    entity.Subscriptions = new ArrayList<>(Arrays.asList(subscription, subscription));

                    return entity;
                });
    }
}
