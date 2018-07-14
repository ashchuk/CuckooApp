package com.ashchuk.cuckooapp.infrastructure.helpers;

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

                    entity.UserGuid = user.Guid;
                    entity.Email = user.Email;
                    entity.Todos = todoItems;
                    entity.DisplayName = user.DisplayName;
                    entity.Status = user.Status.getValue();
                    entity.Messages = messages;
                    entity.Subscriptions = subscriptions;

                    return entity;
                });
    }
}
