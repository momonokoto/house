package com.zpark.repository;

import com.zpark.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    // 查询两人之间的所有私信（支持分页和时间倒序）
    @Query(value = "{$or: [ " +
            "{ $and: [ { 'sender': ?0 }, { 'receiver': ?1 }, { 'type': 'PRIVATE' } ] }, " +
            "{ $and: [ { 'sender': ?1 }, { 'receiver': ?0 }, { 'type': 'PRIVATE' } ] } " +
            "]}", 
            sort = "{timestamp: -1}")
    Page<Message> findPrivateMessages(String user1, String user2, Pageable pageable);

    // 查询最新的N条私信（按时间倒序）
    @Query(value = "{$or: [ " +
            "{ $and: [ { 'sender': ?0 }, { 'receiver': ?1 }, { 'type': 'PRIVATE' } ] }, " +
            "{ $and: [ { 'sender': ?1 }, { 'receiver': ?0 }, { 'type': 'PRIVATE' } ] } " +
            "]}", 
            fields = "{timestamp: 1, content: 1, sender: 1, receiver: 1, type: 1}",
            sort = "{timestamp: 1}")
    List<Message> findTopPrivateMessages(String user1, String user2, int limit);

    // 查询群组消息
    List<Message> findByReceiverAndType(String receiver, Message.MessageType type);

    // 新增：查询早于指定消息的旧消息
    @Query(value = "{$or: [ " +
            "{ $and: [ { 'sender': ?0 }, { 'receiver': ?1 }, { 'type': 'PRIVATE' }, { 'timestamp': { $lt: ?2 } } ] }, " +
            "{ $and: [ { 'sender': ?1 }, { 'receiver': ?0 }, { 'type': 'PRIVATE' }, { 'timestamp': { $lt: ?2 } } ] } " +
            "]}", 
            sort = "{timestamp: 1}")
    Page<Message> findOlderMessages(String user1, String user2, Date timestamp, Pageable pageable);

    // 新增：更新两人之间未读消息为已读
    @Query("{$or: [" +
            "{ $and: [ { 'sender': ?0 }, { 'receiver': ?1 }, { 'type': 'PRIVATE' }, { 'read': false } ] }, " +
            "]}")
    @Update("{$set: {'read': true}}")
    void markMessagesAsRead(String user1, String user2);
}
