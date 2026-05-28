# AI 心理健康助手

一个面向简历项目场景打造的前后台双端 AI 心理健康辅助系统。

## 项目简介

该项目围绕“用户情绪记录 + AI 心理咨询 + 知识内容支持 + 后台风险复核”设计，包含：

- 用户端：首页、知识库、文章详情、AI 咨询、情绪日记、我的记录
- 后台端：管理员登录、数据分析、知识文章管理、咨询记录管理、情绪日志管理
- 服务端：Spring Boot + MyBatis + MySQL，支持登录鉴权、文章增删改查、咨询消息落库、情绪日志落库、多业务分页和近实时数据同步

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Pinia、Element Plus
- 后端：Spring Boot、MyBatis
- 数据库：MySQL 8.0
- AI：DeepSeek 兼容接口

## 当前完成度

### 用户端

- 首页产品介绍
- 用户注册 / 登录
- 知识库列表与文章详情
- AI 咨询对话、历史会话、新建会话、删除会话
- 情绪日记记录与落库
- 我的记录页

### 后台端

- 管理员登录与路由守卫
- 数据分析概览
- 知识文章列表、详情、新增、编辑、删除、分页
- 咨询记录列表、详情、分页
- 情绪日志列表、详情、分页

### 后端

- MySQL 建表脚本与初始化数据
- JWT 鉴权与用户 / 管理员身份隔离
- 文章、咨询、情绪日志相关接口
- AI 咨询消息落库与结构化摘要
- 轮询式近实时数据同步基础

## 目录结构

```txt
ai-vue/
├─ src/                              前端源码
│  ├─ api/                           前端接口层
│  ├─ assets/                        图片与静态资源
│  ├─ components/                    通用组件
│  ├─ router/                        路由配置
│  ├─ stores/                        Pinia 状态管理
│  ├─ styles/                        公共样式
│  ├─ utils/                         映射方法与工具函数
│  ├─ views/                         后台页面
│  └─ views/front/                   用户端页面
├─ springboot-backend/               正式后端
│  ├─ sql/                           建库建表与初始化脚本
│  ├─ pom.xml                        Maven 配置
│  └─ src/main/                      Spring Boot 源码与配置
├─ public/                           Vite 公共资源
├─ images/                           原始图片素材
├─ docs/                             项目文档
│  ├─ FRONTEND_BACKEND联调说明.md
│  ├─ 最终测试清单.md
│  ├─ 最终简历项目说明.md
│  ├─ 简历项目经历终稿.md
│  ├─ 简历项目经历终版-三方向.md
│  ├─ 面试三分钟介绍稿.md
│  ├─ 面试问答最终稿.md
│  ├─ 项目样式.md
│  ├─ 项目理解与面试说明.md
│  └─ 课件.md
├─ package.json
├─ vite.config.js
├─ index.html
└─ .gitignore
```

## 启动方式

### 1. 初始化数据库

执行：

- `springboot-backend/sql/01_schema.sql`
- `springboot-backend/sql/02_seed.sql`

### 2. 启动后端

进入 `springboot-backend/` 后，配置环境变量再启动：

```powershell
cd "D:\实战项目\心理健康助手\code\ai-vue\springboot-backend"
mvn org.springframework.boot:spring-boot-maven-plugin:2.7.18:run
```

健康检查地址：

```txt
http://127.0.0.1:8080/health
```

### 3. 启动前端

在项目根目录执行：

```powershell
cd "D:\实战项目\心理健康助手\code\ai-vue"
npm run dev
```

默认前端通过 Vite 代理访问 `/api`。

## 环境变量

后端建议通过环境变量配置：

- `PORT`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRE_DAYS`
- `DEEPSEEK_BASE_URL`
- `DEEPSEEK_API_KEY`
- `DEEPSEEK_MODEL`

不要把真实数据库密码和 AI Key 提交到 GitHub。

## 项目亮点

- 前后台双端完整闭环，不是单一页面展示项目
- 数据模型完整，覆盖文章、咨询、情绪日志、AI 分析等核心实体
- 基于 Spring Boot + MyBatis + MySQL 实现后端接口和数据持久化
- 接入 DeepSeek 兼容接口，实现心理陪伴式 AI 对话
- 支持登录态隔离、会话管理、近实时数据同步和后台管理

## 文档入口

- [联调说明](docs/FRONTEND_BACKEND联调说明.md)
- [最终测试清单](docs/最终测试清单.md)
- [简历项目说明](docs/最终简历项目说明.md)
- [简历项目经历终稿](docs/简历项目经历终稿.md)
- [三方向简历版本](docs/简历项目经历终版-三方向.md)
- [三分钟介绍稿](docs/面试三分钟介绍稿.md)
- [面试问答最终稿](docs/面试问答最终稿.md)
