# 银行交易系统

## 项目概述

本项目是一个简单的银行交易管理系统，实现了交易记录的创建、查询、修改和删除功能。系统采用内存存储，提供高性能的交易处理能力，并通过缓存机制进一步优化读取性能。

## 技术栈

- **后端**：Spring Boot 3.x, Spring Cache, Spring Validation
- **数据库**：内存存储（ConcurrentHashMap）
- **缓存**：ConcurrentMapCacheManager
- **测试**：JUnit 5, Mockito, JMeter
- **容器化**：Docker
- **构建工具**：Maven

## 主要功能

1. **交易管理**
    - 创建新交易
    - 分页查询交易记录
    - 查询单个交易详情
    - 修改交易信息
    - 删除交易记录

2. **数据验证**
    - 交易金额根据交易类型验证
    - 转账和支付交易必须包含账户
    - 交易类型必须有效
    - 分页查询分页参数必须大于0

3. **性能优化**
    - 内存存储提供快速读写
    - 缓存机制首页默认列表快速查询
    - 支持分页查询大量交易记录

## API 文档

### 交易管理

#### 创建交易POST /api/transactions**请求体**：
{
    "accountId": "ACC1001",
    "amount": 100.00,
    "typeCode": 1,
    "description": "DEPOSIT"
}
#### 获取交易GET /api/transactions/{id}
#### 获取交易列表POST /api/transactions/pageSearch **请求体**：
{
"page":1
"size":10
}
#### 更新交易PUT /api/transactions/{id}**请求体**：{
            "accountId": "ACC1001",
            "amount": 200.00,
            "typeCode": 2,
            "updateAccount": "ACC1001",
            "description": "DEPOSIT"
        }
#### 删除交易DELETE /api/transactions/{id}
## 运行项目

### 使用 Maven 运行
mvn spring-boot:run
### 使用 Docker 运行
# 构建 Docker 镜像
docker build -t transaction-system .

# 运行容器
docker run -p 8080:8080 transaction-system

# 查看服务
docker ps | grep transaction-system
## 测试

### 单元测试
mvn test
### 压力测试

## 依赖库说明

1. **Spring Boot Starter Web**：提供 Web 应用开发能力
2. **Spring Boot Starter Cache**：支持缓存功能
3. **Spring Boot Starter Validation**：提供数据验证功能
4. **JUnit 5**：单元测试框架
5. **Mockito**：模拟测试框架
7. **Lombok**：简化 Java 代码
 
