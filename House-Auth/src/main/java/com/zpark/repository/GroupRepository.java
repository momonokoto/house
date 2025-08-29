//package com.zpark.repository;
//
//import com.zpark.entity.Group;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface GroupRepository extends JpaRepository<Group, String> {
//    // 继承JpaRepository自动获得基础CRUD方法
//    // existsById方法已由JpaRepository提供
//
//    @Query("SELECT g FROM Group g WHERE g.groupId = :groupId AND :member MEMBER OF g.members")
//    Optional<Group> findByGroupIdAndMembersContaining(@Param("groupId") String groupId, @Param("member") String member);
//}