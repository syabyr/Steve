# 使用 Ollama 作为 AI 引擎

## 什么是 Ollama?

Ollama 是一个本地运行大语言模型的工具,具有以下优势:
- **完全免费**: 无需 API key,不产生任何费用
- **隐私保护**: 所有数据在本地处理,不会发送到云端
- **离线运行**: 不需要网络连接
- **性能可控**: 根据硬件配置选择合适的模型

## 安装步骤

### 1. 安装 Ollama

访问 [Ollama 官网](https://ollama.ai/) 下载并安装:

**macOS:**
```bash
brew install ollama
```

**Linux:**
```bash
curl -fsSL https://ollama.ai/install.sh | sh
```

**Windows:**
从官网下载安装程序

### 2. 启动 Ollama 服务

```bash
ollama serve
```

### 3. 下载模型

推荐模型 (按性能和资源需求排序):

**轻量级模型 (8GB RAM+):**
```bash
ollama pull llama3.2
ollama pull mistral
```

**中等模型 (16GB RAM+):**
```bash
ollama pull qwen2.5:14b
ollama pull llama3.1:13b
```

**大型模型 (32GB RAM+, 推荐 GPU):**
```bash
ollama pull deepseek-r1:14b
ollama pull llama3.1:70b
```

### 4. 配置 Steve Mod

编辑 `config/steve-common.toml`:

```toml
[ai]
    provider = "ollama"

[ollama]
    baseUrl = "http://localhost:11434"
    model = "llama3.2"  # 使用你下载的模型
```

### 5. 重启 Minecraft

重启游戏后,Steve 将使用本地 Ollama 模型!

## 模型推荐

| 模型 | 大小 | 最低内存 | 速度 | 质量 | 适用场景 |
|------|------|----------|------|------|----------|
| llama3.2 | 3B | 8GB | ⚡⚡⚡ | ⭐⭐⭐ | 快速测试 |
| mistral | 7B | 8GB | ⚡⚡ | ⭐⭐⭐⭐ | 平衡选择 |
| qwen2.5:14b | 14B | 16GB | ⚡ | ⭐⭐⭐⭐⭐ | 高质量输出 |
| deepseek-r1:14b | 14B | 16GB | ⚡ | ⭐⭐⭐⭐⭐ | 复杂推理 |

## 性能优化

### 使用 GPU 加速

Ollama 会自动检测并使用可用的 GPU:
- **NVIDIA**: 自动使用 CUDA
- **AMD**: 自动使用 ROCm
- **Apple Silicon (M1/M2/M3)**: 自动使用 Metal

### 调整配置

在 `config/steve-common.toml` 中:

```toml
[openai]
    maxTokens = 500      # 减少 token 数量提高速度
    temperature = 0.7    # 降低温度提高一致性
```

## 故障排除

### 1. 连接失败
```
Failed to connect to Ollama at http://localhost:11434
```
**解决方案**: 确保 Ollama 服务正在运行: `ollama serve`

### 2. 模型未找到
```
Make sure model 'llama3.2' is installed
```
**解决方案**: 下载模型: `ollama pull llama3.2`

### 3. 响应缓慢
- 使用更小的模型 (如 llama3.2)
- 减少 maxTokens 设置
- 考虑使用带 GPU 的电脑

### 4. 内存不足
- 使用更小的模型
- 关闭其他占用内存的程序
- 查看可用模型: `ollama list`

## 切换回云端 AI

如果想切换回云端 AI 服务,修改配置:

```toml
[ai]
    provider = "groq"    # 或 "openai", "gemini"
```

## 常用命令

```bash
# 查看已安装模型
ollama list

# 删除模型
ollama rm llama3.2

# 运行模型测试
ollama run llama3.2

# 查看 Ollama 版本
ollama --version

# 更新 Ollama
brew upgrade ollama  # macOS
```

## 更多信息

- [Ollama 官网](https://ollama.ai/)
- [Ollama GitHub](https://github.com/ollama/ollama)
- [模型库](https://ollama.ai/library)
