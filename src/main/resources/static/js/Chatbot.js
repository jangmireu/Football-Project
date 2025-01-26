document.addEventListener("DOMContentLoaded", function () {
    const chatbotToggle = document.getElementById("chatbot-toggle");
    const chatbotWindow = document.getElementById("chatbot-window");
    const chatbotClose = document.getElementById("chatbot-close");
    const chatbotSend = document.getElementById("chatbot-send");
    const chatbotInput = document.getElementById("chatbot-input");
    const chatbotMessages = document.getElementById("chatbot-messages");

    // 챗봇 열기/닫기
    chatbotToggle.addEventListener("click", () => {
        chatbotWindow.style.display = chatbotWindow.style.display === "none" ? "block" : "none";
    });

    chatbotClose.addEventListener("click", () => {
        chatbotWindow.style.display = "none";
    });

    // 메시지 전송 함수
    async function sendMessage() {
        const userInput = chatbotInput.value.trim();
        if (!userInput) return;

        // 사용자 메시지 추가
        chatbotMessages.innerHTML += `<div style="text-align: right; margin: 10px 0;">
            <span style="display: inline-block; background: #f1f1f1; color: #333; padding: 8px 12px; border-radius: 12px; max-width: 80%; font-size: 14px;">
                ${userInput}
            </span>
        </div>`;
        chatbotInput.value = "";

        // 서버에 메시지 전송
        try {
            const response = await fetch("/api/chatbot", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ question: userInput }),
            });

            const data = await response.text();

            // 챗봇 응답 추가
            chatbotMessages.innerHTML += `<div style="text-align: left; margin: 10px 0;">
                <span style="display: inline-block; background: #f1f1f1; color: #333; padding: 8px 12px; border-radius: 12px; max-width: 80%; font-size: 14px;">
                    챗봇: ${data}
                </span>
            </div>`;
            chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
        } catch (error) {
            chatbotMessages.innerHTML += `<div style="text-align: left; margin: 10px 0;">
                <span style="display: inline-block; background: #ffe6e6; color: #d32f2f; padding: 8px 12px; border-radius: 12px; max-width: 80%; font-size: 14px;">
                    챗봇: 서버와 연결할 수 없습니다.
                </span>
            </div>`;
        }
    }

    // 전송 버튼 클릭 이벤트
    chatbotSend.addEventListener("click", sendMessage);

    // 엔터 키 이벤트
    chatbotInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter" && !event.isComposing) {
            event.preventDefault();
            sendMessage();
        }
    });
}); 