# 前后端联调说明

## 1. 数据库

已使用以下脚本初始化：

- `springboot-backend/sql/01_schema.sql`
- `springboot-backend/sql/02_seed.sql`

数据库名为 `ai-vue`。

## 2. 后端环境变量

`backend/.env` 示例：

```env
PORT=1235
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=ai-vue
DB_USER=root
DB_PASSWORD=你的MySQL密码
JWT_SECRET=psychology_ai_secret
```

## 3. 启动后端

当前推荐使用 `springboot-backend/` 目录下的 Spring Boot 后端。

```bash
cd springboot-backend
mvn spring-boot:run
```

健康检查地址：

```txt
http://127.0.0.1:8080/health
```

## 4. 启动前端

```bash
npm install
npm run dev
```

Vite 已将 `/api` 代理到 `http://127.0.0.1:8080`。

## 5. 当前已接入接口

- `POST /api/auth/admin-login`
- `GET /api/auth/profile`
- `GET /api/dashboard/overview`
- `GET /api/articles`
- `GET /api/articles/categories`
- `GET /api/articles/:id`
- `POST /api/articles`
- `PUT /api/articles/:id`
- `DELETE /api/articles/:id`
- `GET /api/consultations`
- `GET /api/consultations/:id`
- `GET /api/emotions`

## 6. 联调策略

- 后端启动时：前端优先读取真实接口
- 后端未启动时：部分页面会回退到本地 mock 数据，便于开发期逐步联调

## 7. 建议联调顺序

1. 登录后台，确认 `admin-login` 和 `profile` 可用
2. 打开文章管理页，验证新增、编辑、删除、分页
3. 打开咨询记录和情绪日志页，验证分页和详情
4. 验证前台知识库是否能读取文章接口
