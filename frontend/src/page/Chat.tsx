import {
  Bubble,
  BubbleProps,
  Sender,
  Welcome,
  XRequest,
  useXAgent,
  useXChat,
} from "@ant-design/x";
import React from "react";
import "./Chat.css";
import { XMarkdown, type ComponentProps } from "@ant-design/x-markdown";
import { Layout, type GetProp, Space } from "antd";
import { SettingOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";
import hljs from "highlight.js";

const { Content } = Layout;

const CodeBlock: React.FC<ComponentProps> = ({ domNode, className, children, lang, block, ...props }) => {
  const codeRef = React.useRef<HTMLElement>(null);
  const language = lang || className?.replace(/language-/, '') || '';
  
  React.useEffect(() => {
    if (codeRef.current && block && language) {
      hljs.highlightElement(codeRef.current);
    }
  }, [children, language, block]);

  if (!block) {
    return <code className={className} {...props}>{children}</code>;
  }

  return (
    <pre>
      <code
        ref={codeRef}
        className={`language-${language} ${className || ''}`}
        {...props}
      >
        {children}
      </code>
    </pre>
  );
};

const renderMarkdown: BubbleProps["messageRender"] = (content) => (
  <div style={{ whiteSpace: 'normal', wordWrap: 'break-word', overflowWrap: 'break-word' }}>
    <XMarkdown 
      content={content} 
      components={{
        code: CodeBlock
      }}
    />
  </div>
);


const roles: GetProp<typeof Bubble.List, "roles"> = {
  ai: {
    placement: "start",
    typing: { step: 5, interval: 20 },
    styles: {
      content: {
        borderRadius: 16,
        backgroundColor: '#faf8f8ff',
      },
    },
  },
  local: {
    placement: "end",
    variant: "shadow",
    styles: {
      content: {
        backgroundColor: '#e4eaf4ff',
      },
    },
  },
};

const Chat: React.FC = () => {
  const navigate = useNavigate();
  const { create } = XRequest({
    baseURL: `/api/v1/assistant/chat/create`
  });
  const { create: completion } = XRequest({
    baseURL: `/api/v1/assistant/chat/completion/stream`
  });

  const [content, setContent] = React.useState("");
  const sessionId = React.useRef("");
  const [agent] = useXAgent({
    request: async (info, { onSuccess, onError, onUpdate }) => {
      if (!sessionId.current) {
        await create(
          {},
          {
            onUpdate: (res) => {
              sessionId.current = res.data.sessionId;
            },
            onSuccess: () => {},
            onError: (err) => {
              onError(err);
            },
          }
        );
      }

      let content = '';

      await completion(
        {
          prompt: info.message,
          sessionId: sessionId.current,
          stream: true,
        },
        {
          
          onUpdate: (res) => {
            const processedData = res.data.replace(/\[@\]/g, '\n').replace(/@|@/g, '\r');
            content += processedData;
            res.data = content;
            onUpdate(res.data);     // 更新累积后的完整内容
          },
          onSuccess: (res) => {
            console.log('onSuccess called:', res);  // 添加调试日志
            onSuccess(res);
          },
          onError: (err) => {
            onError(err);
          },
        }
      );
    },
  });

  const { onRequest, messages } = useXChat({
    agent,
  });

  const onSubmit = (nextContent: string) => {
    if (!nextContent) return;
    onRequest(nextContent);
    setContent("");
  };

  const placeholderNode = (
    <Space direction="vertical" size={16} className="placeholder">
      <Welcome
        variant="borderless"
        icon={<img src={logo} alt="logo" style={{ marginLeft: '30px' }} />}
        title="Hi、我是您的小助理"
        description="快速输入您的问题，我来帮你解答~"
      />
    </Space>
  );

  const items: GetProp<typeof Bubble.List, "items"> = messages.map(
    ({ id, message, status }) => ({
      key: id,
      loading: status === "loading" && !message,
      role: status === "local" ? "local" : "ai",
      content: renderMarkdown(message),
    })
  );

  return (
    <Layout className={`chat-layout`}>
      {messages.length === 0 && (
        <button className="settings-button" onClick={() => navigate('/setting')}>
          <SettingOutlined />
        </button>
      )}
      <Content className={`chat-chat ${items.length > 0 ? "" : "empty"}`}>
        <Bubble.List
          autoScroll
          items={
            items.length > 0
              ? items
              : [{ content: placeholderNode, variant: "borderless" }]
          }
          roles={roles}
          className="messages"
        />
        <Sender
          value={content}
          onSubmit={onSubmit}
          onChange={setContent}
          loading={agent.isRequesting()}
          className="sender"
          placeholder="请输入您的问题"
        />
        <div className="footer">内容由 AI 生成，请仔细甄别</div>
      </Content>
    </Layout>
  );
};


export default Chat;
