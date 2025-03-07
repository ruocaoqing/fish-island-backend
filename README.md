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

## 🎯 贡献指南
感谢您对 **fish-island** 的关注和贡献！欢迎各位开发者参与！🎉

---

### 📌 贡献方式

添加各位想要的数据源，以实现更丰富的内容。

#### 1️⃣ 页面元素抓取

📌 **适用于**：目标网站未提供 API，数据嵌入在 HTML 结构中。

#### ✅ 贡献要求

- **推荐使用**：
   - `Jsoup`（Java）
   - `BeautifulSoup`（Python）
   - `Cheerio`（Node.js）
- **选择器精准**：避免因页面结构变化导致抓取失败。
- **减少 HTTP 请求**：优化抓取效率，避免重复请求。
- **遵守网站爬取规则**（`robots.txt` ）。

#### 💡 示例代码

```java
Document doc = Jsoup.connect("https://example.com").get();
String title = doc.select("h1.article-title").text();
```

---

#### 2️⃣ 页面接口返回数据抓取

📌 **适用于**：目标网站提供 API，可直接调用接口获取 JSON/XML 数据。

#### ✅ 贡献要求

- **推荐使用**：
   - `HttpClient`（Java）
   - `axios`（Node.js）
   - `requests`（Python）
- **分析 API 请求**：确保请求参数完整（`headers`、`cookies`、`token`）。
- **减少不必要的请求**：优化调用频率，避免触发反爬机制。
- **异常处理**：确保代码稳定运行。

#### 💡 示例代码

```java
String apiUrl = "https://api.example.com/data";
String response = HttpRequest.get(apiUrl).execute().body();
JSONObject json = JSON.parseObject(response);
```

---

### 🔗 数据源注册

数据抓取完成后，需要注册数据源，以便系统能够正确使用。

### 🚀 注册流程

1. **添加数据源 Key**：
   `/src/main/java/com/cong/fishisland/model/enums/HotDataKeyEnum.java` 定义新的数据源 Key。

2. **更新数据源映射**：

   -  `/src/main/java/com/lhccong/fish/backend/config/DatabaseConfig.java` 文件中，添加新的数据源配置。

3. **创建数据源类**：

   -  `src/main/java/com/cong/fishisland/datasource` 目录下，新建数据源类，继承 `DataSource`，实现 `getHotPost` 方法。

4. **实现数据获取逻辑**：

   - 按照 `HotPostDataVO` 格式返回数据。
   - 使用 `@Builder` 注解，确保数据能正确解析。

#### 💡 示例代码

```java
HotPostDataVO.builder()
            .title(title)
            .url(url)
            .followerCount(followerCount)
            .excerpt(excerpt)
            .build();
```

---

### 🚀 贡献流程

1. **Fork 仓库** ➜ 点击 GitHub 右上角 `Fork` 按钮。
2. **创建分支** ➜ 推荐使用有意义的分支名，如 `feature/data-scraper-optimization`。
3. **提交代码** ➜ 确保代码可读性高，符合规范。
4. **提交 Pull Request（PR）** ➜ 详细描述您的更改内容，并关联相关 issue（如有）。
5. **等待审核** ➜ 维护者会进行代码审核并合并。

---

### 🎉 感谢您的贡献！

您的每一份贡献都让 **fish-island** 变得更好！💪
