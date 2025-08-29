package com.zpark.service;

import com.zpark.entity.Message;
import com.zpark.repository.MessageRepository;
import com.zpark.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;



    @Autowired
    private RedisUtil redisUtil;

    public void saveMessage(Message message) {
        try {
            messageRepository.save(message);
        } catch (DataAccessException e) {
            logger.error("消息持久化失败 [{}] | 原因: {}", message.getContent(), e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }
    }

    // 查询点对点历史消息
    public List<Message> getPrivateHistory(String user1, String user2,int limit) {
        if (user1 == null || user2 == null || user1.isEmpty() || user2.isEmpty()) {
            throw new IllegalArgumentException("无效的用户参数");
        }
        return messageRepository.findTopPrivateMessages(user1, user2, limit);
    }

    public Page<Message> getPrivateHistory(String user1, String user2, int pageNum, int pageSize) {
        if (user1 == null || user2 == null || user1.isEmpty() || user2.isEmpty()) {
            throw new IllegalArgumentException("无效的用户参数");
        }
        return messageRepository.findPrivateMessages(user1, user2, PageRequest.of(pageNum, pageSize));
    }

    // 滚动到顶部时获取更旧的消息
    public List<Message> getMessages(String sender, String receiver, String message_id) {
        if (sender == null || receiver == null || message_id == null || 
            sender.isEmpty() || receiver.isEmpty() || message_id.isEmpty()) {
            throw new IllegalArgumentException("无效的参数");
        }
        
        // 修改：直接使用Date对象而非Long值
        Message refMessage = messageRepository.findById(message_id)
            .orElseThrow(() -> new IllegalArgumentException("消息不存在"));
        Date refTimestamp = refMessage.getTimestamp();  // 不再使用getTime()
        logger.info("参考消息时间戳: {}", refTimestamp);
        // 修改：使用ASC排序获取真正的旧消息
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "timestamp"));
        Page<Message> page = messageRepository.findOlderMessages(sender, receiver, refTimestamp, pageable);
        
        // 修改：增加空结果检查
        if (page.isEmpty()) {
            logger.info("未找到早于参考消息 {} 的旧消息", message_id);
            return Collections.emptyList();
        }
        
        logger.debug("获取到 {} 条早于参考消息的记录", page.getNumberOfElements());
        return page.getContent();
    }

    // 新增方法：获取用户最近联系人的最新消息（登录时调用）
    public Map<String, List<Message>> getRecentContactsMessages(String username, int limit) {
        if (username == null || username.isEmpty() || limit <= 0) {
            throw new IllegalArgumentException("无效的用户参数或消息数量");
        }
        
        // 获取已排序的最近联系人列表
        Map<String, Double> recentContactsMap = redisUtil.getRecentContactsMap(username, 20);
        // 修改为LinkedHashMap以保持排序顺序
        Map<String, List<Message>> result = new LinkedHashMap<>();

        System.out.println("最近联系人列表: " + recentContactsMap);
        
        // 直接使用已排序的Map进行遍历
        int index = 0;
        for (Map.Entry<String, Double> entry : recentContactsMap.entrySet()) {
            String contact = entry.getKey();
            int queryLimit = (index < 3) ? limit : 1;
            List<Message> messages = messageRepository.findTopPrivateMessages(username, contact, queryLimit);
            result.put(contact, messages);
            index++;
        }
        
        return result;
    }

    // 新增方法：根据ID获取单条消息
    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("消息不存在"));
    }

    // 新增方法：标记指定用户间的消息为已读
    public void markMessagesAsRead(String currentUser, String otherUser) {
        if (currentUser == null || otherUser == null || 
            currentUser.isEmpty() || otherUser.isEmpty()) {
            throw new IllegalArgumentException("无效的用户参数");
        }
        
        try {
            logger.info("标记消息为已读 - 用户1: {}, 用户2: {}", currentUser, otherUser);
            messageRepository.markMessagesAsRead(otherUser, currentUser);
            logger.info("消息标记为已读成功");
        } catch (DataAccessException e) {
            logger.error("标记消息为已读失败 - 用户1: {}, 用户2: {}, 原因: {}", 
                currentUser, otherUser, e.getMessage());
            throw new RuntimeException("消息状态更新失败", e);
        }
    }

}
