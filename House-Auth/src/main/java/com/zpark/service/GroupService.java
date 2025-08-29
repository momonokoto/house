//package com.zpark.service;
//
//import com.zpark.repository.GroupRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class GroupService {
//    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    public boolean isUserInGroup(String userId, String groupId) {
//        try {
//            return groupRepository.findByGroupIdAndMembersContaining(groupId, userId)
//                    .isPresent();
//        } catch (DataAccessException e) {
//            logger.error("成员关系查询失败 [用户{}|群组{}] | 原因: {}", userId, groupId, e.getMessage());
//            throw new RuntimeException("成员关系查询失败", e);
//        }
//    }
//}