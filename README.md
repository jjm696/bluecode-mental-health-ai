# AI Mental Health Assistant

一个基于 Vue 3、Spring Boot、MyBatis、MySQL 和 DeepSeek API 的 AI 心理健康辅助系统。项目采用前后端分离架构，包含用户端心理咨询、情绪日记、知识库阅读，以及管理端咨询记录、情绪日志、文章管理和数据分析等模块。

> 本项目仅用于心理健康陪伴与学习场景展示，不提供医疗诊断、治疗建议或危机干预服务。

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Pinia、Element Plus
- 后端：Java 8、Spring Boot 2.7、MyBatis、JWT
- 数据库：MySQL 8.0
- AI 接入：DeepSeek / OpenAI-Compatible API
- 部署：Railway / Vercel 兼容配置

## 核心功能

- 用户认证：支持用户注册、登录、登录态隔离和路由守卫。
- AI 咨询：支持多轮对话、咨询消息落库、历史会话查看、新建会话和删除会话。
- 情绪日记：支持用户记录每日情绪、文本内容和情绪分析结果。
- 知识库：支持心理健康文章列表、分类浏览和详情阅读。
- 管理后台：支持管理员登录、文章管理、咨询记录查看、情绪日志查看和数据概览。
- 数据分析：统计咨询数量、情绪日志、用户活跃情况等后台运营数据。

## 项目亮点

- 在服务层封装 AI 调用逻辑，统一处理 Prompt、请求参数、异常兜底和返回结果解析。
- 为心理陪伴场景设计助手人设、对话阶段、风险等级判断和固定兜底回复，避免 AI 退化为普通闲聊。
- 使用 JWT 拦截器区分用户端和管理端身份，保证咨询记录、情绪日志等数据按身份隔离。
- 咨询消息和情绪日志持久化到 MySQL，支持前端轮询同步和后台复核。
- 前端拆分用户端和管理端布局，提升双端页面维护性。

## 目录结构

```text
.
├── src/                         # Vue 前端源码
│   ├── api/                     # 接口封装
│   ├── assets/                  # 图片资源
│   ├── components/              # 通用组件
│   ├── router/                  # 路由配置
│   ├── stores/                  # Pinia 状态管理
│   ├── utils/                   # 会话、资源、心理场景工具方法
│   └── views/                   # 用户端与管理端页面
├── springboot-backend/          # Spring Boot 后端服务
│   ├── sql/                     # 建表和初始化数据脚本
│   ├── src/main/java/           # Controller、Service、Mapper、DTO、VO 等
│   └── src/main/resources/application.yml
├── public/                      # Vite 静态资源
├── package.json
├── vite.config.js
└── README.md
```

## 本地运行

### 1. 初始化数据库

创建 MySQL 数据库后依次执行：

```text
springboot-backend/sql/01_schema.sql
springboot-backend/sql/02_seed.sql
```

默认数据库名为 `ai-vue`，也可以通过环境变量覆盖。

### 2. 启动后端

```bash
cd springboot-backend
mvn spring-boot:run
```

后端默认地址：

```text
http://127.0.0.1:8080
```

常用环境变量：

```text
PORT=8080
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=ai-vue
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRE_DAYS=7
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_API_KEY=your_api_key
DEEPSEEK_MODEL=deepseek-chat
```

### 3. 启动前端

```bash
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 安全说明

- 不要提交真实数据库密码、JWT 密钥或 AI API Key。
- 生产环境应使用独立的环境变量配置敏感信息。
- 心理健康类 AI 输出需要保留风险提示和兜底逻辑，避免生成医疗诊断内容。

## 简历展示建议

建议在 GitHub 仓库补充系统截图、在线演示地址和测试账号。README 中的项目亮点可直接支撑“AI 应用接入、Prompt 设计、JSON/结果校验、异常处理、前后端联调和部署”相关面试问题。
