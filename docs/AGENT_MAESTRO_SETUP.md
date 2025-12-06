# Agent Maestro 集成指南

本指南说明如何配置 Steve AI 使用 [Agent Maestro](https://marketplace.visualstudio.com/items?itemName=Joouis.agent-maestro) 的 API 接口。

## 什么是 Agent Maestro？

Agent Maestro 是一个 VS Code 扩展，它提供了 OpenAI/Anthropic/Gemini 兼容的 API 接口，让你可以在本地使用 VS Code 的语言模型（如 GitHub Copilot）来为其他应用提供 AI 能力。

## 优势

- **使用 VS Code 的语言模型**：无需额外的 API key，直接使用你已经订阅的服务（如 GitHub Copilot）
- **本地运行**：API 服务器运行在本地，响应快速
- **兼容性好**：提供标准的 OpenAI API 格式，无需修改大量代码

## 安装步骤

### 1. 安装 Agent Maestro 扩展

1. 在 VS Code 中打开扩展市场
2. 搜索 "Agent Maestro"（发布者：Joouis）
3. 点击安装

### 2. 启动 API 服务器

Agent Maestro 安装后会自动启动 API 服务器，默认端口为 `23333`。

你可以通过以下命令检查服务器状态：
- 打开命令面板（Cmd/Ctrl + Shift + P）
- 输入 "Agent Maestro: Get API Server Status"

### 3. 查看可用模型

Agent Maestro 会自动检测 VS Code 中可用的语言模型。访问以下 URL 查看：

```
http://localhost:23333/api/v1/lm/chatModels
```

常见的可用模型：
- `gpt-4o`（GitHub Copilot）
- `claude-3.5-sonnet`（如果你有 Claude 访问权限）
- `gpt-4-turbo-preview`

### 4. 配置 Steve AI

复制示例配置文件并修改：

```bash
cd /path/to/Steve
cp config/steve-agent-maestro.toml.example config/steve-common.toml
```

或者手动编辑 `config/steve-common.toml`：

```toml
[ai]
    provider = "openai"

[openai]
    # Agent Maestro 不需要真实的 API key，可以填任意值
    apiKey = "agent-maestro"
    
    # Agent Maestro 的 OpenAI 兼容端点
    baseUrl = "http://localhost:23333/api/openai"
    
    # 使用 VS Code 中可用的模型名称
    model = "gpt-4o"
    
    maxTokens = 8000
    temperature = 0.7
```

### 5. 重新编译并运行

```bash
./gradlew build
# 然后启动 Minecraft 并加载 mod
```

## API 端点详情

Agent Maestro 提供以下相关端点：

| 端点 | 用途 |
|------|------|
| `http://localhost:23333/api/openai/chat/completions` | OpenAI 兼容的聊天完成 API |
| `http://localhost:23333/api/anthropic/v1/messages` | Anthropic 兼容 API |
| `http://localhost:23333/api/gemini/v1beta/models/*` | Gemini 兼容 API |
| `http://localhost:23333/api/v1/lm/chatModels` | 查看可用模型列表 |
| `http://localhost:23333/openapi.json` | 完整的 OpenAPI 规范 |

## 配置选项

### 自定义端口

如果需要修改 Agent Maestro 的端口，可以设置环境变量：

```bash
export AGENT_MAESTRO_PROXY_PORT=8080
code .
```

然后在 Steve 配置中相应修改：

```toml
[openai]
    baseUrl = "http://localhost:8080/api/openai"
```

### 工作区级别配置

你也可以在项目的 `.vscode/settings.json` 中配置 Agent Maestro：

```json
{
  "agent-maestro.proxyServerPort": 23333,
  "agent-maestro.mcpServerPort": 23334
}
```

## 故障排除

### 检查服务器状态

在 VS Code 中：
1. 打开命令面板（Cmd/Ctrl + Shift + P）
2. 运行 "Agent Maestro: Get API Server Status"

### 检查端口是否监听

```bash
# macOS/Linux
lsof -i :23333

# 或使用 curl 测试
curl http://localhost:23333/api/v1/lm/chatModels
```

### 常见问题

**Q: API 返回 401 或认证错误**  
A: Agent Maestro 默认不需要认证。如果遇到认证问题，可以在配置中填入任意 apiKey 值。

**Q: 找不到模型**  
A: 确保你已经在 VS Code 中登录了相应的服务（如 GitHub Copilot）。访问 `http://localhost:23333/api/v1/lm/chatModels` 查看可用模型。

**Q: 连接超时**  
A: 确认 Agent Maestro 扩展已安装并激活，可以尝试重启 VS Code 或手动运行 "Agent Maestro: Restart API Server" 命令。

**Q: 与其他 AI 提供商对比**  
A: 
- **Ollama**: 完全本地，完全隐私，但需要下载大模型文件
- **OpenAI/Groq**: 云端服务，需要 API key，按使用量计费
- **Agent Maestro**: 使用你已有的 VS Code 订阅，无额外费用

## 更多信息

- [Agent Maestro 官方文档](https://marketplace.visualstudio.com/items?itemName=Joouis.agent-maestro)
- [Agent Maestro GitHub 仓库](https://github.com/Joouis/agent-maestro)
- [OpenAPI 规范](http://localhost:23333/openapi.json)（需要 Agent Maestro 运行）

## 示例请求

如果你想手动测试 Agent Maestro API：

```bash
curl -X POST http://localhost:23333/api/openai/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer any-value" \
  -d '{
    "model": "gpt-4o",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Hello!"
      }
    ],
    "temperature": 0.7,
    "max_tokens": 100
  }'
```
