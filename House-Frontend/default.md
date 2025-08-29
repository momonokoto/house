# 小众租房平台项目 API


**简介**:小众租房平台项目 API


**HOST**:http://www.xzzf.xyz:8080


**联系人**:


**Version**:1.0


**接口路径**:/v3/api-docs


[TOC]






# 订单管理


## getAll


**接口地址**:`/order/get_ords`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>查询租房的所有订单</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultListOrder|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|msg||string||
|data||array|Order|
|&emsp;&emsp;orderId|订单ID|integer(int64)||
|&emsp;&emsp;orderNo|订单编号|string||
|&emsp;&emsp;tradeNo|支付宝交易号|string||
|&emsp;&emsp;orderTitle|订单标题|string||
|&emsp;&emsp;tenantId|租客ID|integer(int64)||
|&emsp;&emsp;landlordId|房东ID|integer(int64)||
|&emsp;&emsp;propertyId|房源ID|integer(int64)||
|&emsp;&emsp;orderType|订单类型(1长租,2短租)|integer(int32)||
|&emsp;&emsp;rentAmount|月租金|number||
|&emsp;&emsp;depositAmount|押金金额|number||
|&emsp;&emsp;serviceFee|平台服务费|number||
|&emsp;&emsp;totalAmount|订单总金额|number||
|&emsp;&emsp;paymentStatus|支付状态(0未支付,1部分支付,2已支付)|integer(int32)||
|&emsp;&emsp;leaseStartDate|租赁开始日期|string(date)||
|&emsp;&emsp;leaseEndDate|租赁结束日期|string(date)||
|&emsp;&emsp;signingDate|签约日期|string(date-time)||
|&emsp;&emsp;orderStatus|订单状态(0待确认,1已确认,2已入住,3已完成,4已取消)|integer(int32)||
|&emsp;&emsp;cancelReason|取消原因|string||
|&emsp;&emsp;paymentCycle|付款周期(1月付,2季付,3半年付,4年付)|integer(int32)||
|&emsp;&emsp;commissionRate|佣金比例|number||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": [
		{
			"orderId": 0,
			"orderNo": "",
			"tradeNo": "",
			"orderTitle": "花溪大学城公寓",
			"tenantId": 1001,
			"landlordId": 2001,
			"propertyId": 3001,
			"orderType": 1,
			"rentAmount": 5000,
			"depositAmount": 10000,
			"serviceFee": 500,
			"totalAmount": 15500,
			"paymentStatus": 0,
			"leaseStartDate": "2023-01-01",
			"leaseEndDate": "2024-01-01",
			"signingDate": "",
			"orderStatus": 0,
			"cancelReason": "个人原因取消",
			"paymentCycle": 1,
			"commissionRate": 0.05,
			"createdAt": "",
			"updatedAt": ""
		}
	]
}
```


# 房源查找接口


## 房源详情


**接口地址**:`/house/detail/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据房源id查询房源详情</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|房源id|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultHouse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||House|House|
|&emsp;&emsp;ownerId|房东 ID|integer(int64)||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;address|房屋地址|string||
|&emsp;&emsp;area|房屋面积|number(float)||
|&emsp;&emsp;roomNumber|房间数|integer(int32)||
|&emsp;&emsp;roomType|户型|string||
|&emsp;&emsp;rent|租金|number||
|&emsp;&emsp;latitude|房屋的纬度坐标，用于地图定位|number(float)||
|&emsp;&emsp;longitude|房屋的经度坐标，用于地图定位|number(float)||
|&emsp;&emsp;status|房屋状态（1-待租，2-已租）|integer(int32)||
|&emsp;&emsp;region|地区|string||
|&emsp;&emsp;description|房源描述|string||
|&emsp;&emsp;img|图片|string||
|&emsp;&emsp;video|视频|string||
|&emsp;&emsp;elevator|是否电梯|integer(int32)||
|&emsp;&emsp;detailedAddress|详细地址|string||
|&emsp;&emsp;verify|审核状态 （0-待审核 1-已审核）|integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"ownerId": 0,
		"title": "",
		"address": "",
		"area": 0,
		"roomNumber": 0,
		"roomType": "",
		"rent": 0,
		"latitude": 0,
		"longitude": 0,
		"status": 0,
		"region": "",
		"description": "",
		"img": "",
		"video": "",
		"elevator": 0,
		"detailedAddress": "",
		"verify": 0
	}
}
```


## 房源查询


**接口地址**:`/house/find`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页展示房源</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|house|房源查询条件|query|true|房源查询条件对象|房源查询条件对象|
|&emsp;&emsp;region|地区||false|string||
|&emsp;&emsp;detailedAddress|详细地址||false|string||
|&emsp;&emsp;minPrice|最小价格||false|integer(int32)||
|&emsp;&emsp;maxPrice|最大价格||false|integer(int32)||
|&emsp;&emsp;minArea|最小面积||false|integer(int32)||
|&emsp;&emsp;maxArea|最大面积||false|integer(int32)||
|Authorization|认证令牌|header|true|string||
|currentPage|当前页码|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultPageHouse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||PageHouse|PageHouse|
|&emsp;&emsp;records||array|House|
|&emsp;&emsp;&emsp;&emsp;ownerId|房东 ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;address|房屋地址|string||
|&emsp;&emsp;&emsp;&emsp;area|房屋面积|number(float)||
|&emsp;&emsp;&emsp;&emsp;roomNumber|房间数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;roomType|户型|string||
|&emsp;&emsp;&emsp;&emsp;rent|租金|number||
|&emsp;&emsp;&emsp;&emsp;latitude|房屋的纬度坐标，用于地图定位|number(float)||
|&emsp;&emsp;&emsp;&emsp;longitude|房屋的经度坐标，用于地图定位|number(float)||
|&emsp;&emsp;&emsp;&emsp;status|房屋状态（1-待租，2-已租）|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;region|地区|string||
|&emsp;&emsp;&emsp;&emsp;description|房源描述|string||
|&emsp;&emsp;&emsp;&emsp;img|图片|string||
|&emsp;&emsp;&emsp;&emsp;video|视频|string||
|&emsp;&emsp;&emsp;&emsp;elevator|是否电梯|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;detailedAddress|详细地址|string||
|&emsp;&emsp;&emsp;&emsp;verify|审核状态 （0-待审核 1-已审核）|integer(int32)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageHouse|PageHouse|
|&emsp;&emsp;searchCount||PageHouse|PageHouse|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"records": [
			{
				"ownerId": 0,
				"title": "",
				"address": "",
				"area": 0,
				"roomNumber": 0,
				"roomType": "",
				"rent": 0,
				"latitude": 0,
				"longitude": 0,
				"status": 0,
				"region": "",
				"description": "",
				"img": "",
				"video": "",
				"elevator": 0,
				"detailedAddress": "",
				"verify": 0
			}
		],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [
			{
				"column": "",
				"asc": true
			}
		],
		"optimizeCountSql": {},
		"searchCount": {},
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	}
}
```


# 房源预约接口


## 预约房源


**接口地址**:`/appointment/{houseId}`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>预约房源</p>



**请求示例**:


```javascript
{
  "date": "",
  "message": "你好明天下午有时间吗",
  "phone": 15692712421,
  "name": "谢先生"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|houseId|房源ID|path|true|string||
|Authorization|认证令牌|header|true|string||
|预约信息|预约信息|body|true|预约信息|预约信息|
|&emsp;&emsp;date|预约时间||true|string(date-time)||
|&emsp;&emsp;message|预约留言||true|string||
|&emsp;&emsp;phone|预约人电话||true|integer(int64)||
|&emsp;&emsp;name|预约人姓名||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResult预约信息|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||预约信息|预约信息|
|&emsp;&emsp;date|预约时间|string(date-time)||
|&emsp;&emsp;message|预约留言|string||
|&emsp;&emsp;phone|预约人电话|integer(int64)||
|&emsp;&emsp;name|预约人姓名|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"date": "",
		"message": "你好明天下午有时间吗",
		"phone": 15692712421,
		"name": "谢先生"
	}
}
```


# 个人中心接口


## 取消预约


**接口地址**:`/personal_center/cancel_appointment/{appointmentId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>取消预约</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|appointmentId|预约id|path|true|integer(int64)||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultAppointment请求实体|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||Appointment请求实体|Appointment请求实体|
|&emsp;&emsp;appointmentId|预约ID|integer(int64)||
|&emsp;&emsp;tenantId|关联的租客ID|integer(int64)||
|&emsp;&emsp;houseId|关联的房源ID|integer(int64)||
|&emsp;&emsp;date|预约时间|string(date-time)||
|&emsp;&emsp;status|状态 (0-已取消, 1-已确认, 2-待处理)|integer(int32)||
|&emsp;&emsp;message|预约留言|string||
|&emsp;&emsp;createTime|预约创建时间|string(date-time)||
|&emsp;&emsp;ownerId|房东 ID|integer(int64)||
|&emsp;&emsp;phone|预约人电话|integer(int64)||
|&emsp;&emsp;name|预约人姓名|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"date": "",
		"message": "你好明天下午有时间吗",
		"phone": 15692712421,
		"name": "谢先生"
	}
}
```


## 修改密码


**接口地址**:`/personal_center/change_password`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>修改密码</p>



**请求示例**:


```javascript
{
  "oldPassword": "12345678",
  "newPassword": "12312312",
  "confirmNewPassword": "12312312"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|changePasswordCondition请求实体|更改密码所需参数|body|true|ChangePasswordCondition请求实体|ChangePasswordCondition请求实体|
|&emsp;&emsp;oldPassword|旧密码||true|string||
|&emsp;&emsp;newPassword|新密码||true|string||
|&emsp;&emsp;confirmNewPassword|确认新密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultUser|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||User|User|
|&emsp;&emsp;id|用户ID|integer(int64)||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;password|密码|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;userNickname|用户昵称|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;role|角色|string||
|&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;introduction|个人简介|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"id": 0,
		"username": "",
		"password": "",
		"phone": "",
		"userNickname": "",
		"email": "",
		"role": "",
		"status": 0,
		"introduction": ""
	}
}
```


## 我预约的房源


**接口地址**:`/personal_center/my_appointment`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页查询我预约的房源</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|currentPage|当前页码|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultPageAppointment请求实体|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||PageAppointment请求实体|PageAppointment请求实体|
|&emsp;&emsp;records||array|Appointment请求实体|
|&emsp;&emsp;&emsp;&emsp;appointmentId|预约ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;tenantId|关联的租客ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;houseId|关联的房源ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;date|预约时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;status|状态 (0-已取消, 1-已确认, 2-待处理)|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;message|预约留言|string||
|&emsp;&emsp;&emsp;&emsp;createTime|预约创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;ownerId|房东 ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;phone|预约人电话|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;name|预约人姓名|string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageAppointment请求实体|PageAppointment请求实体|
|&emsp;&emsp;searchCount||PageAppointment请求实体|PageAppointment请求实体|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"records": [
			{
				"date": "",
				"message": "你好明天下午有时间吗",
				"phone": 15692712421,
				"name": "谢先生"
			}
		],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [
			{
				"column": "",
				"asc": true
			}
		],
		"optimizeCountSql": {},
		"searchCount": {},
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	}
}
```


## 我的收藏


**接口地址**:`/personal_center/my_collection`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页查询我的收藏</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|currentPage|当前页码|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultPageCollection请求实体|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||PageCollection请求实体|PageCollection请求实体|
|&emsp;&emsp;records||array|Collection请求实体|
|&emsp;&emsp;&emsp;&emsp;id|收藏id主键|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;userId|收藏者的id|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;houseId|收藏房源的id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|收藏时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;logicalDel|逻辑删除，0代表没有删除，1代表删除|integer(int32)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageCollection请求实体|PageCollection请求实体|
|&emsp;&emsp;searchCount||PageCollection请求实体|PageCollection请求实体|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"records": [
			{
				"houseId": 1
			}
		],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [
			{
				"column": "",
				"asc": true
			}
		],
		"optimizeCountSql": {},
		"searchCount": {},
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	}
}
```


## 我发布的房源


**接口地址**:`/personal_center/my_house`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页查询我发布的房源</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|currentPage|当前页码|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultPageHouse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||PageHouse|PageHouse|
|&emsp;&emsp;records||array|House|
|&emsp;&emsp;&emsp;&emsp;ownerId|房东 ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;address|房屋地址|string||
|&emsp;&emsp;&emsp;&emsp;area|房屋面积|number(float)||
|&emsp;&emsp;&emsp;&emsp;roomNumber|房间数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;roomType|户型|string||
|&emsp;&emsp;&emsp;&emsp;rent|租金|number||
|&emsp;&emsp;&emsp;&emsp;latitude|房屋的纬度坐标，用于地图定位|number(float)||
|&emsp;&emsp;&emsp;&emsp;longitude|房屋的经度坐标，用于地图定位|number(float)||
|&emsp;&emsp;&emsp;&emsp;status|房屋状态（1-待租，2-已租）|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;region|地区|string||
|&emsp;&emsp;&emsp;&emsp;description|房源描述|string||
|&emsp;&emsp;&emsp;&emsp;img|图片|string||
|&emsp;&emsp;&emsp;&emsp;video|视频|string||
|&emsp;&emsp;&emsp;&emsp;elevator|是否电梯|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;detailedAddress|详细地址|string||
|&emsp;&emsp;&emsp;&emsp;verify|审核状态 （0-待审核 1-已审核）|integer(int32)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageHouse|PageHouse|
|&emsp;&emsp;searchCount||PageHouse|PageHouse|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"records": [
			{
				"ownerId": 0,
				"title": "",
				"address": "",
				"area": 0,
				"roomNumber": 0,
				"roomType": "",
				"rent": 0,
				"latitude": 0,
				"longitude": 0,
				"status": 0,
				"region": "",
				"description": "",
				"img": "",
				"video": "",
				"elevator": 0,
				"detailedAddress": "",
				"verify": 0
			}
		],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [
			{
				"column": "",
				"asc": true
			}
		],
		"optimizeCountSql": {},
		"searchCount": {},
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	}
}
```


## 修改个人资料


**接口地址**:`/personal_center/update`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>修改个人资料</p>



**请求示例**:


```javascript
{
  "userNickname": "",
  "email": "",
  "phone": "",
  "introduction": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|编辑个人资料|编辑个人资料所需参数|body|true|编辑个人资料|编辑个人资料|
|&emsp;&emsp;userNickname|用户昵称||true|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;phone|手机号||false|string||
|&emsp;&emsp;introduction|个人简介||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResult编辑个人资料|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||编辑个人资料|编辑个人资料|
|&emsp;&emsp;userNickname|用户昵称|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;introduction|个人简介|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"userNickname": "",
		"email": "",
		"phone": "",
		"introduction": ""
	}
}
```


# 管理员接口


## 清除登录缓存


**接口地址**:`/api/admin/evict-cache`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>强制清除所有用户的 redis 登录缓存（通常用于紧急情况或缓存更新）</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|缓存清除成功||
|500|缓存清除失败||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 设置房源出租状态


**接口地址**:`/api/admin/setHouseStatus`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>设置房源出租状态（0-待出租 1-已出租）</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|houseId||query|true|integer(int32)||
|status|房源状态（0-待出租 1-已出租）,可用值:0,1|query|true|integer||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 设置房源审核状态


**接口地址**:`/api/admin/setHouseVerify`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>设置房源审核或禁用状态（0-待审核 1-已审核），禁用则标记为0-待审核</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|houseId||query|true|integer(int32)||
|verify|设置房源审核或禁用状态（0-待审核 1-已审核），禁用则标记为0-待审核,可用值:0,1|query|true|integer||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 设置用户状态


**接口地址**:`/api/admin/setUserStatus`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>设置用户状态（1-正常 0-禁用）</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|username||query|true|string||
|status|用户状态（1:正常 0:禁用）,可用值:0,1|query|true|integer||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 认证处理中心接口


## 管理员登录


**接口地址**:`/api/user/admin/authenticate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>仅限管理员账号登录</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|用于登录|用于登录|body|true|用于登录|用于登录|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|登录成功|JwtResponse|
|403|非管理员账号尝试登录||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|accessToken||string||
|tokenType||string||


**响应示例**:
```javascript
{
	"accessToken": "",
	"tokenType": ""
}
```


## 用户登录


**接口地址**:`/api/user/authenticate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>通用用户登录接口，返回JWT令牌</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|用于登录|用于登录|body|true|用于登录|用于登录|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|登录成功|JwtResponse|
|401|登录失败（用户名或密码错误）||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|accessToken||string||
|tokenType||string||


**响应示例**:
```javascript
{
	"accessToken": "",
	"tokenType": ""
}
```


## 用户登出


**接口地址**:`/api/user/logout`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>使当前用户的Token失效</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|refresh_token|退出登录|cookie|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|登出成功||
|500|服务端异常||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 刷新访问令牌


**接口地址**:`/api/user/refresh-token`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>通过有效的Refresh Token获取新的Access Token</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|refresh_token|刷新令牌（从Cookie自动携带）|cookie|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|刷新成功|JwtResponse|
|401|Refresh Token无效或已过期||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|accessToken||string||
|tokenType||string||


**响应示例**:
```javascript
{
	"accessToken": "",
	"tokenType": ""
}
```


## 用户注册


**接口地址**:`/api/user/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>通过邮箱验证码完成用户注册</p>



**请求示例**:


```javascript
{
  "id": 0,
  "username": "",
  "password": "",
  "email": "",
  "角色": "",
  "code": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|注册用|注册用|body|true|注册用|注册用|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||
|&emsp;&emsp;email|邮箱||true|string||
|&emsp;&emsp;角色|||false|string||
|&emsp;&emsp;code|验证码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|注册成功||
|400|注册失败（验证码错误/用户已存在等）||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 发送验证码


**接口地址**:`/api/user/resendemail`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向用户邮箱发送新的验证码（用于注册）</p>



**请求示例**:


```javascript
{
  "username": "",
  "email": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|用于发送邮件接口|用于发送邮件接口|body|true|用于发送邮件接口|用于发送邮件接口|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;email|邮箱||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|发送成功|UserResult|
|400|发送失败（邮箱无效或服务异常）||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|token||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"token": ""
}
```


# 实名认证接口


## 实名认证


**接口地址**:`/realNameAuthentication`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>实名认证</p>



**请求示例**:


```javascript
{
  "realName": "谢茂驰",
  "idCard": "522225200212183215",
  "mobile": "15692712421"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|实名认证请求参数|实名认证请求参数|body|true|实名认证请求参数|实名认证请求参数|
|&emsp;&emsp;realName|姓名||true|string||
|&emsp;&emsp;idCard|身份证号||true|string||
|&emsp;&emsp;mobile|手机号||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": ""
}
```


# 收藏接口


## 收藏房源


**接口地址**:`/collection/add/{houseId}`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据房源id收藏房源</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|houseId|房源id|path|true|integer(int32)||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultCollection请求实体|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||Collection请求实体|Collection请求实体|
|&emsp;&emsp;id|收藏id主键|integer(int64)||
|&emsp;&emsp;userId|收藏者的id|integer(int64)||
|&emsp;&emsp;houseId|收藏房源的id|integer(int32)||
|&emsp;&emsp;createTime|收藏时间|string(date-time)||
|&emsp;&emsp;logicalDel|逻辑删除，0代表没有删除，1代表删除|integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"houseId": 1
	}
}
```


## 取消收藏


**接口地址**:`/collection/del/{houseId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据房源id取消收藏</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|houseId|房源id|path|true|integer(int32)||
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultCollection请求实体|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||Collection请求实体|Collection请求实体|
|&emsp;&emsp;id|收藏id主键|integer(int64)||
|&emsp;&emsp;userId|收藏者的id|integer(int64)||
|&emsp;&emsp;houseId|收藏房源的id|integer(int32)||
|&emsp;&emsp;createTime|收藏时间|string(date-time)||
|&emsp;&emsp;logicalDel|逻辑删除，0代表没有删除，1代表删除|integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": {
		"houseId": 1
	}
}
```


# 提交房源接口


## 上传房源信息及媒体文件


**接口地址**:`/api/files/upload`


**请求方式**:`POST`


**请求数据类型**:`multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:<p>支持同时上传房源JSON数据、图片和视频文件</p>



**请求示例**:


```javascript
{
  "ownerId": 0,
  "title": "",
  "address": "",
  "area": 0,
  "roomNumber": 0,
  "roomType": "",
  "rent": 0,
  "latitude": 0,
  "longitude": 0,
  "status": 0,
  "region": "",
  "description": "",
  "img": "",
  "video": "",
  "elevator": 0,
  "detailedAddress": "",
  "verify": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|house|房源JSON数据|body|true|House|House|
|&emsp;&emsp;ownerId|房东 ID||false|integer(int64)||
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;address|房屋地址||true|string||
|&emsp;&emsp;area|房屋面积||true|number(float)||
|&emsp;&emsp;roomNumber|房间数||true|integer(int32)||
|&emsp;&emsp;roomType|户型||true|string||
|&emsp;&emsp;rent|租金||true|number||
|&emsp;&emsp;latitude|房屋的纬度坐标，用于地图定位||false|number(float)||
|&emsp;&emsp;longitude|房屋的经度坐标，用于地图定位||false|number(float)||
|&emsp;&emsp;status|房屋状态（1-待租，2-已租）||true|integer(int32)||
|&emsp;&emsp;region|地区||true|string||
|&emsp;&emsp;description|房源描述||true|string||
|&emsp;&emsp;img|图片||false|string||
|&emsp;&emsp;video|视频||false|string||
|&emsp;&emsp;elevator|是否电梯||true|integer(int32)||
|&emsp;&emsp;detailedAddress|详细地址||true|string||
|&emsp;&emsp;verify|审核状态 （0-待审核 1-已审核）||false|integer(int32)||
|imageFile|房源图片文件|query|false|file||
|videoFile|房源视频文件|query|false|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|上传成功||
|400|参数错误（JSON解析失败/文件格式错误）||
|401|未授权（Token无效或过期）||
|500|服务器内部错误（文件存储失败等）||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 支付宝回调接口


## notifyFun


**接口地址**:`/alipay/callback/pay_notify`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>支付异步回调接口</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## syncFun


**接口地址**:`/alipay/callback/pay_sync`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>支付同步回调接口, 支付成功后返回的页面</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 支付宝退款接口


## refund


**接口地址**:`/alipay/refund`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>支付宝退款接口</p>



**请求示例**:


```javascript
{
  "orderId": 1932450603288416258,
  "applyAmount": 5000,
  "refundReason": "提前退租"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|rentalRefundDto|租房退款请求参数|body|true|RentalRefundDto|RentalRefundDto|
|&emsp;&emsp;orderId|关联订单ID，需要退款的订单id||false|integer(int64)||
|&emsp;&emsp;applyAmount|申请退款金额，不能大于订单的交易金额||false|number||
|&emsp;&emsp;refundReason|退款原因||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": ""
}
```


# 支付宝支付接口


## createOrder


**接口地址**:`/alipay/create_order`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建订单, 返回一个支付链接，前端得到链接打开即可支付</p>



**请求示例**:


```javascript
{
  "propertyId": 21,
  "orderType": 1,
  "leaseStartDate": "2023-01-01",
  "leaseDuration": 2
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||
|orderDto|订单实体|body|true|OrderDto|OrderDto|
|&emsp;&emsp;propertyId|房源ID||true|integer(int64)||
|&emsp;&emsp;orderType|订单类型(1长租,2短租)||false|integer(int32)||
|&emsp;&emsp;leaseStartDate|选择租期开始日期||false|string(date)||
|&emsp;&emsp;leaseDuration|租期: 前端给用户选择1个月，3个月， 6,个月，12,个月||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResponseResultString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|jsonData||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"jsonData": ""
}
```


# AI接口


## 处理用户文本输入


**接口地址**:`/ai/process`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>接收一个包含用户文本的JSON请求。例如：<code>{&quot;text&quot;: &quot;你好&quot;}</code></p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|认证令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# RabbitMQ接口


## 发送群消息


**接口地址**:`/api/messages/group`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向指定群组发送广播消息</p>



**请求示例**:


```javascript
{
  "groupId": "",
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|groupMessageDto|消息内容传输对象|body|true|GroupMessageDto|GroupMessageDto|
|&emsp;&emsp;groupId|群组id||true|string||
|&emsp;&emsp;content|消息||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|发送成功||
|400|发送失败||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取群组历史


**接口地址**:`/api/messages/history/group`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>查询指定群组的聊天历史记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|groupId|群组ID|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|查询成功||
|403|无访问权限||
|500|服务器错误||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取私聊记录


**接口地址**:`/api/messages/history/private`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>查询与指定用户的私聊历史消息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|otherUser|对方用户名|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|查询成功||
|500|服务器错误||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 发送私信


**接口地址**:`/api/messages/private`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向指定用户发送点对点私信消息</p>



**请求示例**:


```javascript
{
  "receiver": "",
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|privateMessageDto|消息内容传输对象|body|true|PrivateMessageDto|PrivateMessageDto|
|&emsp;&emsp;receiver|接收者用户名||true|string||
|&emsp;&emsp;content|消息||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|发送成功||
|400|发送失败||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 发送私信_静态队列


**接口地址**:`/api/messages/static/private`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向指定用户发送私信消息</p>



**请求示例**:


```javascript
{
  "receiver": "",
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|privateMessageDto|消息内容传输对象|body|true|PrivateMessageDto|PrivateMessageDto|
|&emsp;&emsp;receiver|接收者用户名||true|string||
|&emsp;&emsp;content|消息||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|发送成功||
|400|发送失败||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# user-controller


## getex


**接口地址**:`/user1/get`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|User|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id|用户ID|integer(int64)|integer(int64)|
|username|用户名|string||
|password|密码|string||
|phone|手机号|string||
|userNickname|用户昵称|string||
|email|邮箱|string||
|role|角色|string||
|status|状态|integer(int32)|integer(int32)|
|introduction|个人简介|string||


**响应示例**:
```javascript
{
	"id": 0,
	"username": "",
	"password": "",
	"phone": "",
	"userNickname": "",
	"email": "",
	"role": "",
	"status": 0,
	"introduction": ""
}
```