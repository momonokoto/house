package com.zpark.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "messages")
public class Message  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String sender;
    private String receiver; // 可以是用户名或群组ID
    private String content;
    private Date timestamp = new Date();
    private MessageType type; // PRIVATE 或 GROUP
    private boolean read = false;

    public enum MessageType {
        PRIVATE, GROUP, System
    }
}
