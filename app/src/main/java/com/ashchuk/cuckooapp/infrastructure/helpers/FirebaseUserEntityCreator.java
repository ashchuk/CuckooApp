package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.MessagesRepository;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.TodoItemsRepositiry;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;

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
}
