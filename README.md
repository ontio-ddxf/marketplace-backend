# marketplace-backend

[接口规范](#接口规范)
	- [工具接口](#工具接口)
		- [查询订单](#查询订单)
		- [获取数据](#获取数据)
        
        
## 工具接口

### 查询订单

1. 提供 `ontid`和用户类型以及分页参数。
2. 查询 `ontid` 所对应的订单列表并分页返回。

```text
url：/api/v1/data-dealer/tools/orders/{type}?{ontid=}&{pageNum=}&{pageSize=}
method：GET
```

- 请求：


| Field_Name |  Type  |   Description   |
|:----------:|:------:|:---------------------------------------------------------------------:|
|  type   | Integer | 查询用户类型:0-需求方;1-提供方|
|  ontid  |  String | ontid |
|  pageNum  |  Integer | 当前页码 |
|  pageSize  |  Integer | 每页记录数 |

- 响应：

```json
{
	"action": "queryList",
	"code": 0,
	"msg": "SUCCESS",
	"result": {
		"total": 6,
		"list": [{
			"orderId": "17ae271f38e4ad28500c68651354f2a6953d9f3d",
			"dataDemander": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
			"dataProvider": "did:ont:AUJjTER6xUkfSwh2GApyrgxFRZn7ib8cix",
			"dataIdList": [
				"3664666335316561306436353431383862306663383261663335613263366139",
				"3664666335316561306436353431383862306663383261663335613263366139"
			],
			"buyDate": "2019-05-08 11:28:26",
			"state": "boughtOnchain",
			"isRecvMsg": 0,
			"isRecvToken": 0
		}],
		"pageNum": 1,
		"pageSize": 1,
		"size": 1,
		"startRow": 1,
		"endRow": 1,
		"pages": 6,
		"prePage": 0,
		"nextPage": 2,
		"isFirstPage": true,
		"isLastPage": false,
		"hasPreviousPage": false,
		"hasNextPage": true,
		"navigatePages": 8,
		"navigatepageNums": [
			1,
			2,
			3,
			4,
			5,
			6
		],
		"navigateFirstPage": 1,
		"navigateLastPage": 6,
		"firstPage": 1,
		"lastPage": 6
	},
	"version": "v1"
}
```

| Field_Name |  Type  |          Description          |
|:----------:|:------:|:-----------------------------:|
|   action   | String |           动作标志            |
|  version   | String |            版本号             |
|   code     |  int   |            错误码             |
|    msg    | String | 成功为SUCCESS，失败为错误描述 |
|   result   | String | 成功返回订单列表分页信息，失败返回"" |


### 获取数据
1. 提供 买家ontid 和订单号。
2. 获取订单号对应的数据信息。

```text
url：/api/v1/data-dealer/tools/data/{orderId=}&{ontid=}
method：GET
```

- 请求：


| Field_Name |  Type  |   Description   |
|:----------:|:------:|:---------------------------------------------------------------------:|
|  orderId   | String | 订单号 |
|  ontid  |  String | 买家ontid |


- 响应：

```json
{
	"action": "getData",
	"code": 0,
	"msg": "SUCCESS",
	"result": [
		"data"
	],
	"version": "v1"
}
```

| Field_Name |  Type  |          Description          |
|:----------:|:------:|:-----------------------------:|
|   action   | String |           动作标志            |
|  version   | String |            版本号             |
|   code     |  int   |            错误码             |
|    msg    | String | 成功为SUCCESS，失败为错误描述 |
|   result   | String | 成功返回订单号对应的数据信息，失败返回"" |