##  EasyAsk – 你的本地知识问答助手

**只需指定一个文件夹，EasyAsk 就能将你电脑里的文档变成一个智能、私有的问答助手。**

你可以用自然语言提问，例如：
> “我们的退货政策是什么？”  
> “管理员密码怎么重置？” 

easy-ask 会从你指定目录中的 **Markdown、Word、TXT 等文件**中自动查找信息，并给出准确回答。

✅ **离线运行** – 数据始终留在你的电脑  
✅ **零配置上手** – 指定目录即可使用，无需复杂设置   
✅ **免费开源** – MIT 协议，个人与商业项目均可自由使用

无论是**开发者、学生，还是被大量文档困扰的职场人**，EasyAsk 都能帮你快速唤醒沉睡在硬盘里的知识，打造属于你自己的“私有 ChatGPT”。

![](./public/images/flow.png)


```plaintext
EasyAsk 架构
├── 用户层：自然语言提问
├── 核心层：AI 模型自动选择工具
│   ├── 工具1：本地向量库检索
│   ├── 工具2：读取文件列表
│   ├── 工具3：分块读取文件内容
│   └── 工具4：后台深度检索任务
├── 存储层
│   ├── 本地文档文件夹（PDF/Word/TXT/MD）
│   └── 本地向量库（自动缓存问答对）
└── 输出层：生成回答并自动优化向量库
```

## 快速安装

### Maven 安装与运行

> **⚠️ 版本要求：**
> - Java 版本：建议 Java 21+
> - Maven 版本：建议 Maven 3.6+
>
> 请先检查版本：
> ```bash
>  java -version
>  mvn -version
> ```

1. 克隆项目
```bash
   git clone https://github.com/lvmaizi/easy-ask.git
```

2. 编译项目

```bash
  mvn clean package -DskipTests -pl backend
```

3. 配置模型及文件夹路径
```bash
    vi ./scripts/conf/application.properties
```

4. 启动项目

```bash
    sh ./scripts/start.sh
```
```declarative
打开浏览器，访问：👉 http://localhost:8080
```
### Docker 安装与运行
1. 克隆项目
```bash
   git clone https://github.com/lvmaizi/easy-ask.git
   cd easy-ask/backend
```
2. 配置模型
```bash
    vi ./scripts/conf/application.properties
```
3. 构建镜像
```bash
    docker build -t easy-ask:latest .
```
4. 挂载文件夹启动容器
```bash
    docker run -d \
      --name easy-ask \
      -p 8080:8080 \
      -v /user/path:/app/docs \
      -e JAVA_OPTS="-Xms512m -Xmx1g -Dbase.path=/app/docs" \
      easy-ask:latest
```
```declarative
打开浏览器，访问：👉 http://localhost:8080
```

输入你的问题，Enjoy！


## 📄 许可证

本项目采用 MIT License — 自由使用、修改、分发，无论个人或商业用途。

## 🙌 贡献与反馈

欢迎提交 Issue、PR 或 Star 本项目！  
如果你觉得 EasyAsk 帮到了你，请点个 ⭐️ 支持开源！

GitHub: https://github.com/lvmaizi/easy-ask
作者：@lvmaizi

你的知识，值得被轻松找到。  
EasyAsk — 私有、安全、高效。
