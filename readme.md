## 简介
libs 目录中需要导入nihaopay sdk 才能运行，将libs作为依赖库，支持eclipse
需要配置token才能完成交易

## 版本要求
Android SDK 要求 Android 2.3 及以上版本  
请使用 Java 7 或以上版本

##关于SDK

### 一、发起支付
组装支付报文，与token构造NihaopayTask, 发起支付，必须异步发起。 
具体参数类型参见
[securepay](http://docs.nihaopay.com/api/v1.1/#create-a-securepay-transaction)


### 二、获取支付状态

从 NihaopayResult中获得支付结果。支付成功后，用户服务器也会收到nihaopay 服务器发送的异步通知。 最终支付成功请根据服务端异步通知为准。
具体参数类型参见
[securepay](http://docs.nihaopay.com/api/v1.1/#create-a-securepay-transaction)

##注意事项
* 引入了fastjson.jar


