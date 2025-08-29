<template>
  <div class="chat-container">
    <!-- <div class="chat-header">
      <h1>小众租房平台 DeepSeek Chat 智能助手</h1>
    </div> -->
    <div class="chat-messages" ref="chatMessages">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['chat-message', msg.sender]"
      >
        {{ msg.content }}
      </div>
      <div v-if="isTyping" class="typing-indicator">
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
      </div>
    </div>
    <div class="chat-input">
      <input
        type="text"
        v-model="question"
        @keydown.enter="sendMessage"
        placeholder="输入消息..."
      />
      <button :disabled="!question.trim() || isTyping" @click="sendMessage">
        <svg viewBox="0 0 24 24">
          <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
        </svg>
        发送
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      question: '',
      messages: [],
      isTyping: false,
    }
  },
  methods: {
    async sendMessage() {
      if (!this.question.trim() || this.isTyping) return;

      const query = this.question.trim();
      this.messages.push({ sender: 'user', content: query });
      this.question = '';
      this.isTyping = true;
      this.scrollToBottom();

      // 预创建 bot 消息占位
      const botMsg = { sender: 'bot', content: '' };
      this.messages.push(botMsg);
      this.scrollToBottom();

      try {
        const response = await fetch('http://www.xzzf.xyz/deepSeek/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Accept: 'text/event-stream',
          },
          body: JSON.stringify({ question: query }),
        })

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data:')) {
              const data = line.replace(/^data:\s*/, '').trim()
              if (data === '[DONE]') {
                this.isTyping = false
                return
              }
              botMsg.content += data
              this.scrollToBottom()
            }
          }
        }
      } catch (err) {
        botMsg.content = '暂时无法处理您的请求，请稍后再试。'
        this.isTyping = false
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatMessages
        container.scrollTop = container.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
:root {
  --primary-color: #5b8cff;
  --user-bg: linear-gradient(135deg, #5b8cff 0%, #3d6ef7 100%);
  --bot-bg: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
  --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.chat-container {
  width: 100%;
  max-width: 800px;
  height: 90vh;
  margin: 40px auto;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: var(--shadow);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 24px;
  background: var(--primary-color);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-header h1 {
  margin: 0;
  font-size: 1.8rem;
}

.chat-messages {
  flex: 1;
  padding: 0px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-message {
  max-width: 75%;
  padding: 16px 20px;
  border-radius: 20px;
  line-height: 1.5;
  position: relative;
  word-break: break-word;
  animation: messageAppear 0.3s ease-out;
}

.chat-message.user {
  background: var(--user-bg);
  color: white;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
  box-shadow: var(--shadow);
}

.chat-message.bot {
  background: var(--bot-bg);
  color: #2d3748;
  align-self: flex-start;
  border-bottom-left-radius: 4px;
  box-shadow: var(--shadow);
}

.chat-input {
  /* padding: 20px; */
  margin-bottom: 100px;
  background: rgba(255, 255, 255, 0.9);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  gap: 12px;
}

.chat-input input {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.8);
}

.chat-input button {
  padding: 12px 24px;
  border: none;
  border-radius: 16px;
  background:rgba(64, 108, 255, 0.5);
  transition: 0.4s;
  color: white;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.chat-input button:hover {
  background: #406cff;
}

.chat-input button:disabled {
  background: #c2d1ff;
  cursor: not-allowed;
}

.chat-input button svg {
  width: 18px;
  height: 18px;
  fill: currentColor;
}

.typing-indicator {
  display: inline-flex;
  gap: 6px;
  padding: 12px 20px;
  background: var(--bot-bg);
  border-radius: 20px;
  align-self: flex-start;
}

.typing-dot {
  width: 8px;
  height: 8px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}
.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

@keyframes messageAppear {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
