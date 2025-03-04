# 摸鱼岛后端

> 作者：[@lhccong](https://github.com/lhccong)  
> 贡献者：[@Shingbb](https://github.com/Shingbb)

## 项目简介

摸鱼岛后端是一个用Java开发的项目，旨在为摸鱼岛应用提供后端服务。项目采用了以下技术栈：

- **后端框架**：Spring Boot、Netty
- **数据库**：MySQL、Redis
- **持久层框架**：MyBatis
- **容器化**：Docker
- 
## 功能特性

- **用户管理**：提供用户注册、登录、权限管理等功能。
- **数据处理**：实现业务数据的增删改查操作，确保数据的一致性和完整性。
- **接口设计**：为前端提供 RESTful API，支持 JSON 格式的数据交换。
- **安全保障**：集成安全机制，保护用户数据和系统安全。



## 项目结构

```
├── .mvn/                 # Maven包装器相关文件
├── doc/                  # 项目文档
├── sql/                  # 数据库脚本
├── src/                  # 源代码
├── .gitignore            # Git忽略文件配置
├── Dockerfile            # Docker构建文件
├── lombok.config         # Lombok配置文件
├── mvnw                  # Maven包装器可执行文件（Unix）
├── mvnw.cmd              # Maven包装器可执行文件（Windows）
└── pom.xml               # Maven项目对象模型文件
```

## 快速开始

### 环境要求

- **Java 11** 或更高版本
- **Maven 3.6** 或更高版本
- **Docker**（可选，用于容器化部署）

### 构建与运行

1. **克隆仓库**

   ```bash
   git clone https://github.com/lhccong/fish-island-backend.git
   cd fish-island-backend
   ```

2. **构建项目**

   使用Maven构建项目：

   ```bash
   ./mvnw clean install
   ```

3. **运行应用**

   ```bash
   java -jar target/your-app.jar
   ```

   或者使用Docker运行：

   ```bash
   docker build -t fish-island-backend .
   docker run -p 8123:8123 fish-island-backend
   ```

## 贡献指南

欢迎任何形式的贡献！如果你有新的想法、发现了问题或想改进代码，请提交Issue或Pull Request。

## 许可证

本项目采用MIT许可证。详细信息请参阅[LICENSE](LICENSE)文件。

## 联系方式

如有任何问题或建议，请联系项目作者：[@lhccong](https://github.com/lhccong) 