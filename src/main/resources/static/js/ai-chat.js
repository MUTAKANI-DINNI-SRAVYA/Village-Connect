/* VillageConnect AI Chat Page Logic */

// Suggested prompts matching database seed data
const SUGGESTED_PROMPTS = {
    en: [
        "Where can I buy organic neem fertilizer?",
        "Which shops sell medicines in Kanakalagunta?",
        "Can I get house wiring services nearby?",
        "Are there any tomato seeds in stock?"
    ],
    te: [
        "నేను సేంద్రీయ వేప ఎరువును ఎక్కడ కొనగలను?",
        "కనకలగుంటలో ఏ దుకాణాలలో మందులు అమ్ముతారు?",
        "నాకు దగ్గర్లో ఇళ్ళ వైరింగ్ సేవలు దొరుకుతాయా?",
        "టమోటా విత్తనాలు అందుబాటులో ఉన్నాయా?"
    ]
};

document.addEventListener('DOMContentLoaded', () => {
    // Check if user is logged in (AI chat requires authentication)
    if (!auth.isAuthenticated()) {
        window.location.href = 'login.html?redirect=ai-chat.html';
        return;
    }

    const chatForm = document.getElementById('chat-form');
    const chatInput = document.getElementById('chat-input');
    const chatMessages = document.getElementById('chat-messages');
    const suggestionsContainer = document.getElementById('suggested-prompts-container');

    // Populate suggested prompt buttons based on active language
    function loadSuggestions() {
        if (!suggestionsContainer) return;
        const lang = currentLang;
        const prompts = SUGGESTED_PROMPTS[lang] || SUGGESTED_PROMPTS.en;
        
        suggestionsContainer.innerHTML = '';
        prompts.forEach(p => {
            const btn = document.createElement('button');
            btn.className = 'btn btn-outline-teal btn-sm me-2 mb-2 text-start rounded-pill py-2 px-3 border-teal';
            btn.style.color = '#0f766e';
            btn.style.borderColor = '#0f766e';
            btn.innerText = p;
            btn.addEventListener('click', () => {
                chatInput.value = p;
                chatInput.focus();
            });
            suggestionsContainer.appendChild(btn);
        });
    }

    // Append a message to the chat window
    function appendMessage(sender, text) {
        const msgDiv = document.createElement('div');
        msgDiv.className = `message ${sender}`;
        
        // Convert Markdown-like styling from Gemini response to simple HTML breaks/bullet formatting
        let formattedText = text
            .replace(/\n/g, '<br>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/-\s(.*?)(<br>|$)/g, '<li>$1</li>');
        
        if (formattedText.includes('<li>')) {
            // Wrap bullet lists with ul
            formattedText = formattedText.replace(/(<li>.*?<\/li>)+/g, '<ul>$&</ul>');
        }

        msgDiv.innerHTML = formattedText;
        chatMessages.appendChild(msgDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // Append loading bubble
    function showTypingIndicator() {
        const loadingDiv = document.createElement('div');
        loadingDiv.className = 'message bot typing-indicator-msg';
        loadingDiv.id = 'typing-indicator';
        loadingDiv.innerHTML = `
            <div class="d-flex align-items-center gap-1">
                <span class="spinner-grow spinner-grow-sm text-teal" role="status" style="width: 8px; height: 8px;"></span>
                <span class="spinner-grow spinner-grow-sm text-teal" role="status" style="width: 8px; height: 8px; animation-delay: 0.2s"></span>
                <span class="spinner-grow spinner-grow-sm text-teal" role="status" style="width: 8px; height: 8px; animation-delay: 0.4s"></span>
            </div>
        `;
        chatMessages.appendChild(loadingDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // Remove loading bubble
    function removeTypingIndicator() {
        const indicator = document.getElementById('typing-indicator');
        if (indicator) {
            indicator.remove();
        }
    }

    // Submit user message
    if (chatForm) {
        chatForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const message = chatInput.value.trim();
            if (!message) return;

            // Clear input and append user message
            chatInput.value = '';
            appendMessage('user', message);

            // Show loading
            showTypingIndicator();

            try {
                const response = await apiCall('/ai/chat', {
                    method: 'POST',
                    body: JSON.stringify({ message: message })
                });
                
                removeTypingIndicator();
                if (response && response.reply) {
                    appendMessage('bot', response.reply);
                } else {
                    appendMessage('bot', "No response received. Please check backend configurations.");
                }
            } catch (err) {
                removeTypingIndicator();
                appendMessage('bot', `Error communicating with AI: ${err.message}`);
            }
        });
    }

    // Listen for language toggling to refresh suggestions
    const btnTe = document.getElementById('btn-lang-te');
    const btnEn = document.getElementById('btn-lang-en');
    if (btnTe) btnTe.addEventListener('click', () => setTimeout(loadSuggestions, 50));
    if (btnEn) btnEn.addEventListener('click', () => setTimeout(loadSuggestions, 50));

    // Voice Input Integration using Web Speech API
    const btnVoiceInput = document.getElementById('btn-voice-input');
    if (btnVoiceInput) {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            btnVoiceInput.style.display = 'none';
        } else {
            const recognition = new SpeechRecognition();
            recognition.continuous = false;
            recognition.interimResults = false;
            let isListening = false;
            let isStarting = false;

            btnVoiceInput.addEventListener('click', () => {
                if (isListening || isStarting) {
                    try {
                        recognition.stop();
                    } catch (e) {
                        console.error("Speech recognition stop error:", e);
                    }
                    return;
                }
                
                isStarting = true;
                recognition.lang = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 'te-IN' : 'en-IN';
                try {
                    recognition.start();
                } catch (e) {
                    console.error("Speech recognition start error:", e);
                    isStarting = false;
                }
            });

            recognition.onstart = () => {
                isStarting = false;
                isListening = true;
                btnVoiceInput.classList.add('btn-voice-listening');
                chatInput.placeholder = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 
                    'మీరు మాట్లాడేటప్పుడు వింటున్నాము...' : 'Listening... Speak now...';
            };

            recognition.onend = () => {
                isStarting = false;
                isListening = false;
                btnVoiceInput.classList.remove('btn-voice-listening');
                chatInput.placeholder = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 
                    'టైప్ చేయండి...' : 'Type your query here...';
            };

            recognition.onerror = (event) => {
                console.error("Speech recognition error:", event.error);
                isStarting = false;
                isListening = false;
                btnVoiceInput.classList.remove('btn-voice-listening');
                
                if (event.error === 'not-allowed') {
                    chatInput.placeholder = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 
                        'మైక్రోఫోన్ అనుమతి నిరాకరించబడింది...' : 'Microphone permission denied...';
                } else {
                    chatInput.placeholder = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 
                        'స్వర ఇన్‌పుట్ లోపం...' : 'Voice input error...';
                }
                
                setTimeout(() => {
                    chatInput.placeholder = (typeof currentLang !== 'undefined' && currentLang === 'te') ? 
                        'టైప్ చేయండి...' : 'Type your query here...';
                }, 3000);
            };

            recognition.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                chatInput.value = transcript;
                chatInput.focus();
            };
        }
    }

    // Load initial configurations
    loadSuggestions();
});
