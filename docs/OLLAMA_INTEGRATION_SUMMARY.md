# Ollama 集成完成总结

## 已完成的修改

### 1. 新增文件
- **`OllamaClient.java`**: 实现了与 Ollama API 的通信
  - 支持本地 Ollama 服务器连接
  - 使用聊天 API 格式 (`/api/chat`)
  - 完整的错误处理和日志记录

### 2. 修改的文件

#### `SteveConfig.java`
- 添加了 `OLLAMA_BASE_URL` 配置项 (默认: `http://localhost:11434`)
- 添加了 `OLLAMA_MODEL` 配置项 (默认: `llama3.2`)
- 将默认 AI provider 改为 `ollama`
- 更新了 AI_PROVIDER 注释,包含 ollama 选项

#### `TaskPlanner.java`
- 添加了 `OllamaClient` 实例
- 在 `getAIResponse()` 方法中添加了 "ollama" case
- 将 ollama 设为未知 provider 的默认选项

#### `steve-common.toml.example`
- 添加了 `[ai]` 配置段
- 添加了 `[ollama]` 配置段,包含 baseUrl 和 model
- 包含使用说明注释

### 3. 新增文档
- **`docs/OLLAMA_SETUP.md`**: 完整的 Ollama 设置指南
  - 安装步骤
  - 模型推荐
  - 性能优化建议
  - 故障排除方法

## 使用方法

### 快速开始

1. **安装 Ollama**:
   ```bash
   brew install ollama  # macOS
   ```

2. **启动服务**:
   ```bash
   ollama serve
   ```

3. **下载模型**:
   ```bash
   ollama pull llama3.2
   ```

4. **配置文件**: 编辑 `config/steve-common.toml`
   ```toml
   [ai]
       provider = "ollama"
   
   [ollama]
       baseUrl = "http://localhost:11434"
       model = "llama3.2"
   ```

5. **重启游戏**: 启动 Minecraft,Steve 现在使用本地 AI!

## 优势

✅ **完全免费** - 无需 API key 或付费订阅  
✅ **隐私保护** - 数据不离开本地设备  
✅ **离线工作** - 无需互联网连接  
✅ **可定制** - 可选择不同大小和能力的模型  
✅ **兼容性** - 仍然支持原有的 OpenAI/Gemini/Groq  

## 切换 AI Provider

只需修改配置中的 `provider` 字段:

```toml
[ai]
    provider = "ollama"   # 本地 Ollama
    # provider = "groq"   # 免费云端 (最快)
    # provider = "gemini" # Google Gemini
    # provider = "openai" # OpenAI GPT
```

## 注意事项

- Ollama 需要在后台运行 (`ollama serve`)
- 首次使用需要下载模型 (可能较大,如 4-8GB)
- 性能取决于硬件配置 (推荐有 GPU)
- 如果响应慢,可以换用更小的模型

## 测试

编译并运行游戏后:
1. 创建/召唤一个 Steve 实体
2. 给它发送命令
3. 查看日志确认使用的是 Ollama:
   ```
   [steve] Requesting AI plan for Steve 'Steve' using ollama: ...
   ```

祝使用愉快! 🎮🤖
