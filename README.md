# 房屋租赁系统

一个基于Spring Boot开发的房屋租赁平台，提供房源发布、查找、预约、支付等全流程服务。

## 项目简介

房屋租赁系统是一个综合性的租房平台，旨在为房东和租客提供便捷的房屋租赁服务。系统包含用户认证、房源管理、预约看房、在线支付、后台管理等功能模块，支持现代化的租房业务流程。

## 技术架构

### 后端技术栈
- **核心框架**: Spring Boot 3.2.3
- **编程语言**: Java 18
- **数据库**: MySQL 8.0.33
- **缓存**: Redis
- **搜索引擎**: Elasticsearch
- **消息队列**: RabbitMQ
- **文档数据库**: MongoDB
- **安全框架**: Spring Security + JWT
- **持久层框架**: MyBatis Plus
- **API文档**: Knife4j 4.5.0
- **支付接口**: 支付宝SDK
- **AI服务**: DeepSeek API
- **文件存储**: 七牛云


## 功能模块

### 用户系统
- 用户注册与登录
- 实名认证
- 个人中心管理
- 消息通知系统

### 房源管理
- 房源信息发布
- 房源详情展示
- 房源搜索与筛选
- 地图找房功能

### 租赁流程
- 在线预约看房
- 收藏心仪房源
- 在线支付租金
- 租赁合同管理

### 后台管理
- 房源审核与管理
- 用户信息管理
- 订单处理
- 系统配置管理

### AI助手
- 智能客服
- 房源推荐
- 问答服务

## 项目结构
HouseSystem/
 ├── Common/ # 公共模块，包含通用实体类、工具类、配置等 
 ├── House-Admin/ # 后台管理模块 
 ├── House-AliPay/ # 支付宝支付模块 
 ├── House-Auth/ # 认证授权模块 
 ├── House-DeepSeek/ # AI助手模块（集成DeepSeek API） 
 ├── House-FindHouse/ # 找房模块 
 ├── House-PersonalCenter/# 个人中心模块 
 ├── House-Publish/ # 房源发布模块 
 └── House-Start/ # 系统启动模块

## 环境要求

- JDK 18+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- MongoDB 4.4+
- Elasticsearch 7.0+
- RabbitMQ 3.8+

## 配置说明

系统主要配置在`House-Start/src/main/resources/application.yml`文件中：

- 数据库连接配置
- Redis连接配置
- MongoDB连接配置
- Elasticsearch连接配置
- 支付宝SDK配置
- 七牛云存储配置

## 部署步骤

### 后端部署

1. 克隆项目代码
2. 添加(HouseSystem\House-Start\src\main\resources\application.yml)中的各项配置
3. 在MySQL中创建数据库并执行初始化脚本
4. 确保Redis、MongoDB、Elasticsearch、RabbitMQ服务正常运行
5. 使用Maven构建项目：mvn clean install
6. 启动项目：java -jar House-Start/target/House-Start-0.0.1-SNAPSHOT.jar
   