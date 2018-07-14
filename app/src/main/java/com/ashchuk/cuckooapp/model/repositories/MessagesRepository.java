package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class MessagesRepository {

    public static Observable<List<Message>> getMessages() {
        return CuckooApp.getDatabase().messageDAO().getMessages()
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<List<Message>> getMessagesByUserId(String userId) {
        return CuckooApp.getDatabase().messageDAO().getMessagesByUserId(userId)
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertMessage(Message message) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase()
                        .messageDAO()
                        .insert(message));
    }

}
