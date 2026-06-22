/* =====================================================
   VillageConnect — app.js
   Core module: Auth state, API calls, Navbar, i18n
   ===================================================== */

/* ---------- API Base ---------- */
const API_BASE = '/api';

/* ---------- Auth Module ---------- */
const auth = {
    _TOKEN_KEY: 'vc_token',
    _USER_KEY: 'vc_user',

    setToken(token, user) {
        localStorage.setItem(this._TOKEN_KEY, token);
        localStorage.setItem(this._USER_KEY, JSON.stringify(user));
    },

    getToken() {
        return localStorage.getItem(this._TOKEN_KEY);
    },

    getUser() {
        try {
            return JSON.parse(localStorage.getItem(this._USER_KEY));
        } catch {
            return null;
        }
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    logout() {
        localStorage.removeItem(this._TOKEN_KEY);
        localStorage.removeItem(this._USER_KEY);
        window.location.href = 'login.html';
    },

    clearAndRedirect() {
        localStorage.removeItem(this._TOKEN_KEY);
        localStorage.removeItem(this._USER_KEY);
        window.location.href = 'login.html';
    }
};

/* ---------- API Call Helper ---------- */
async function apiCall(endpoint, options = {}) {
    const token = auth.getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };

    const config = {
        ...options,
        headers: { ...headers, ...(options.headers || {}) }
    };

    const response = await fetch(`${API_BASE}${endpoint}`, config);

    if (response.status === 401 || response.status === 403) {
        auth.clearAndRedirect();
        throw new Error('Session expired. Please log in again.');
    }

    if (!response.ok) {
        let errorMsg = `Request failed (${response.status})`;
        try {
            const errData = await response.json();
            errorMsg = errData.error || errData.message || errorMsg;
        } catch (_) {}
        throw new Error(errorMsg);
    }

    // Handle empty bodies (e.g. 204 No Content)
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    }
    return {};
}

/* ---------- i18n / Translations ---------- */
let currentLang = localStorage.getItem('vc_lang') || 'en';

const TRANSLATIONS = {
    en: {
        appName: 'VillageConnect',
        home: 'Home',
        shops: 'Shops',
        chatbot: 'AI Chatbot',
        login: 'Login',
        register: 'Register',
        logout: 'Logout',
        dashboard: 'Dashboard',
        welcomeBack: 'Welcome Back',
        signInSubtitle: 'Sign in to search shops and manage orders',
        emailAddress: 'Email Address',
        passwordLabel: 'Password',
        emailPlaceholder: 'name@example.com',
        passwordPlaceholder: 'Enter password',
        signInBtn: 'Sign In',
        dontHaveAccount: "Don't have an account?",
        registerHere: 'Register here',
        products: 'Products',
        services: 'Services',
        reviews: 'Reviews',
        all: 'All Categories',
        groceries: 'Groceries',
        agriculture: 'Agriculture',
        electrical: 'Electrical',
        medical: 'Medical',
        clothing: 'Clothing',
        food: 'Food & Dining',
        stationery: 'Stationery & Books',
        footwear: 'Footwear',
        mobiles: 'Mobile Services',
        tailoring: 'Tailoring',
        saloon: 'Saloon',
        dairy: 'Dairy & Poultry',
        automobile: 'Automobile',
        carpentry: 'Carpentry',
        construction: 'Construction',
        veterinary: 'Veterinary',
        plumbing: 'Plumbing',
        vegetables: 'Fruits & Vegetables',
        kitchenware: 'Kitchenware',
    },
    te: {
        appName: 'విలేజ్‌కనెక్ట్',
        home: 'హోమ్',
        shops: 'దుకాణాలు',
        chatbot: 'AI చాట్‌బాట్',
        login: 'లాగిన్',
        register: 'నమోదు',
        logout: 'లాగ్అవుట్',
        dashboard: 'డాష్‌బోర్డ్',
        welcomeBack: 'స్వాగతం',
        signInSubtitle: 'దుకాణాలు వెతకడానికి లాగిన్ అవ్వండి',
        emailAddress: 'ఇమెయిల్ చిరునామా',
        passwordLabel: 'పాస్‌వర్డ్',
        emailPlaceholder: 'name@example.com',
        passwordPlaceholder: 'పాస్‌వర్డ్ నమోదు చేయండి',
        signInBtn: 'లాగిన్ అవ్వండి',
        dontHaveAccount: 'ఖాతా లేదా?',
        registerHere: 'ఇక్కడ నమోదు చేయండి',
        products: 'ఉత్పత్తులు',
        services: 'సేవలు',
        reviews: 'సమీక్షలు',
        all: 'అన్ని వర్గాలు',
        groceries: 'కిరాణా',
        agriculture: 'వ్యవసాయం',
        electrical: 'విద్యుత్',
        medical: 'వైద్యం',
        clothing: 'దుస్తులు',
        food: 'ఆహారం & భోజనం',
        stationery: 'స్టేషనరీ & పుస్తకాలు',
        footwear: 'పాదరక్షలు',
        mobiles: 'మొబైల్ సేవలు',
        tailoring: 'టైలరింగ్',
        saloon: 'సెలూన్',
        dairy: 'పాల & పౌల్ట్రీ',
        automobile: 'ఆటోమొబైల్',
        carpentry: 'వడ్రంగం',
        construction: 'నిర్మాణం',
        veterinary: 'పశువైద్యం',
        plumbing: 'ప్లంబింగ్',
        vegetables: 'పళ్ళు & కూరగాయలు',
        kitchenware: 'వంటగది సామాన్లు',
    }
};

function applyTranslations(lang) {
    currentLang = lang;
    localStorage.setItem('vc_lang', lang);
    document.querySelectorAll('[data-translate]').forEach(el => {
        const key = el.dataset.translate;
        if (TRANSLATIONS[lang] && TRANSLATIONS[lang][key]) {
            el.innerText = TRANSLATIONS[lang][key];
        }
    });
    // Update button active state
    const btnEn = document.getElementById('btn-lang-en');
    const btnTe = document.getElementById('btn-lang-te');
    if (btnEn && btnTe) {
        if (lang === 'te') {
            btnEn.className = 'btn btn-outline-light btn-sm';
            btnTe.className = 'btn btn-light btn-sm';
        } else {
            btnEn.className = 'btn btn-light btn-sm';
            btnTe.className = 'btn btn-outline-light btn-sm';
        }
    }
}

/* ---------- Navbar Auth Section ---------- */
function renderNavbarAuth() {
    const navSection = document.getElementById('navbar-auth-section');
    if (!navSection) return;

    if (auth.isAuthenticated()) {
        const user = auth.getUser();
        let dashLink = 'index.html';
        if (user) {
            if (user.role === 'ADMIN') dashLink = 'dashboard-admin.html';
            else if (user.role === 'SHOP_OWNER') dashLink = 'dashboard-owner.html';
            else dashLink = 'dashboard-customer.html';
        }
        navSection.innerHTML = `
            <li class="nav-item me-2">
                <a class="nav-link" href="${dashLink}">
                    <i class="bi bi-person-circle"></i> ${user ? user.name : 'Dashboard'}
                </a>
            </li>
            <li class="nav-item">
                <button class="btn btn-outline-light btn-sm rounded-pill px-3" onclick="auth.logout()">
                    <i class="bi bi-box-arrow-right me-1"></i><span data-translate="logout">Logout</span>
                </button>
            </li>`;
    } else {
        navSection.innerHTML = `
            <li class="nav-item me-2">
                <a class="nav-link" href="login.html" data-translate="login">Login</a>
            </li>
            <li class="nav-item">
                <a class="btn btn-warning btn-sm rounded-pill px-3 fw-semibold" href="register.html" data-translate="register">Register</a>
            </li>`;
    }
}

/* ---------- Init on DOM Ready ---------- */
document.addEventListener('DOMContentLoaded', () => {
    renderNavbarAuth();
    applyTranslations(currentLang);

    // Language toggle buttons
    const btnEn = document.getElementById('btn-lang-en');
    const btnTe = document.getElementById('btn-lang-te');
    if (btnEn) btnEn.addEventListener('click', () => applyTranslations('en'));
    if (btnTe) btnTe.addEventListener('click', () => applyTranslations('te'));
});
