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
│   ├── 工具1：读取文件列表
│   ├── 工具2：分块读取文件内容
│   ├── 工具3：本地向量库检索
│   └── 工具4：后台深度检索任务
├── 存储层
│   ├── 本地文档文件夹（PDF/Word/TXT/MD）
│   └── 本地向量库（自动缓存问答对）
└── 输出层：生成回答并自动优化向量库
```

## 快速上手

### 下载客户端

<table style="width: 100%">
  <tr>
    <td width="25%" align="center">
      <b>Windows</b>
    </td>
    <td width="25%" align="center" colspan="1">
      <b>MacOS</b>
    </td>
    <td width="25%" align="center">
      <b>Linux</b>
    </td>
  </tr>
  <tr style="text-align: center">
    <td align="center" valign="middle">
      <a href='https://github.com/lvmaizi/easy-ask/releases'>
        <b>exe</b>
      </a>
    </td>
    <td align="center" valign="middle">
      <a href='https://github.com/lvmaizi/easy-ask/releases'>
        <b>mac</b>
      </a>
    </td>
    <td align="center" valign="middle">
      <a href='https://github.com/lvmaizi/easy-ask/releases'>
        <b>sh</b>
      </a>
    </td>
  </tr>
</table>

### Mac 安装与运行
```declarative
1. 下载客户端，双击安装

打开「系统设置」 (System Settings) -> 「隐私与安全性」 (Privacy & Security) -> 仍要打开

2. 配置模型密钥

创建配置文件：${user.home}/easy-ask/conf/application.properties

model.url={文本模型调用地址}
model.apiKey={密钥}
model.name={使用的模型名称}

embedding.model.url={向量模型调用地址}
embedding.model.apiKey={密钥}
embedding.model.name={向量模型名称}

server.port=8080

3. 将相关文档移动至${user.home}/easy-ask目录下

4. 双击运行客户端

5. 访问 http://localhost:8080
```

### Linux 安装与运行
```declarative

1. 下载并解压 easy-ask.zip

2. 确保jdk版本 > 21

java -version

3. 修改配置文件：conf/application.properties

4. 运行 start.sh
```

### Windows 安装与运行

```declarative


```

### 开始使用

打开浏览器，访问：

👉 http://localhost:8080

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
