# Yu AI Agent — 基于 Spring AI 的全栈 AI 智能体应用

基于 **Spring Boot 3、Spring AI、Spring AI Alibaba、通义千问和 Vue 3** 开发的全栈 AI Agent 项目，涵盖大模型对话、RAG 知识库、工具调用、ReAct 智能体和 SSE 流式交互等技术。

## 项目简介

本项目围绕 AI 应用开发与智能体技术展开，主要实现两个应用：

- **AI 恋爱大师**：支持 AI 问答和多轮对话，探索大模型在情感咨询场景中的应用。
- **YuManus 超级智能体**：基于 ReAct 思想实现任务分析、工具选择、工具执行和结果反馈。

项目采用前后端分离架构，Java 后端负责大模型调用与业务逻辑，Vue 3 前端负责用户交互。

## 核心功能

- 大模型接入：基于 Spring AI Alibaba 对接阿里云百炼 Qwen 模型
- 多轮对话：使用 ChatMemory 管理上下文
- RAG 检索增强：结合文档加载、向量存储和相似度检索
- 工具调用：支持自定义工具及智能体工具执行
- ReAct 智能体：实现 Think → Act 循环
- SSE 交互：向前端推送智能体执行进度和结果
- 前后端分离：Spring Boot REST API + Vue 3

## 技术栈

| 分类 | 技术 |
|---|---|
| 后端语言 | Java 21 |
| 后端框架 | Spring Boot 3 |
| AI 框架 | Spring AI、Spring AI Alibaba |
| 大语言模型 | Qwen（阿里云百炼） |
| 数据库 | PostgreSQL |
| 向量检索 | PgVector |
| 前端框架 | Vue 3、Vite |
| 构建工具 | Maven、npm |
| 部署 | GitHub、Vercel、Java 云服务 |

## 项目结构

```text
yu-ai-agent1/
├── src/main/java/          # Java 后端源代码
├── src/main/resources/     # 配置文件及项目资源
├── yu-ai-agent1-frontend/  # Vue 3 前端
├── pom.xml                 # Maven 依赖
├── Dockerfile              # Docker 部署配置
└── README.md               # 项目说明
```

## 本地运行

### 1. 后端

准备 JDK 21、Maven 和项目所需环境变量。

根据实际使用的功能配置阿里云百炼 API Key、数据库连接和搜索 API Key。

```powershell
.\mvnw.cmd spring-boot:run
```

默认后端端口为 `8123`，API 前缀为 `/api`。

### 2. 前端

```powershell
cd yu-ai-agent1-frontend
npm install
npm run dev
```

开发环境默认访问 `http://localhost:5173`。

## 在线演示

待公网部署完成后补充 Vercel 前端地址及后端部署信息。

## 后续计划

- [ ] 完成前后端公网部署
- [ ] 完善智能体执行过程展示
- [ ] 增加接口鉴权与访问频率限制
- [ ] 优化 RAG 检索效果
- [ ] 补充项目运行截图和架构图

## 说明

本项目主要用于 AI Agent 技术学习、工程实践及个人作品展示。使用前请自行配置合法有效的 API Key，并妥善保管数据库凭证。
