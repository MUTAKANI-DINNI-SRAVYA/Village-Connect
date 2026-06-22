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

/* ---------- Category Default Cover Images ---------- */
const CATEGORY_DEFAULT_IMAGES = {
    'Groceries': [
        'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800',
        'https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=800',
        'https://images.unsplash.com/photo-1604719312566-8912e9227c6a?w=800',
        'https://images.unsplash.com/photo-1583258292688-d0213df4a3a8?w=800'
    ],
    'Agriculture': [
        'https://images.unsplash.com/photo-1589923188900-85dae523342b?w=800',
        'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800',
        'https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=800',
        'https://images.unsplash.com/photo-1592982537447-6f2a6a0c8188?w=800'
    ],
    'Electrical': [
        'https://images.unsplash.com/photo-1558211583-d26f610c1eb1?w=800',
        'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=800'
    ],
    'Medical': [
        'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800',
        'https://images.unsplash.com/photo-1586015555751-63bb77f4322a?w=800',
        'https://images.unsplash.com/photo-1607619056574-7b8f304b3b33?w=800'
    ],
    'Clothing': [
        'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=800',
        'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800',
        'https://images.unsplash.com/photo-1479064555552-3ef4979f8908?w=800'
    ],
    'Food & Dining': [
        'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=800',
        'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800',
        'https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=800'
    ],
    'Stationery': [
        'https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=800',
        'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=800'
    ],
    'Footwear': [
        'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800',
        'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800'
    ],
    'Mobile Services': [
        'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800',
        'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800'
    ],
    'Tailoring': [
        'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=800',
        'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?w=800'
    ],
    'Saloon': [
        'https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=800',
        'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800'
    ],
    'Dairy & Poultry': [
        'https://images.unsplash.com/photo-1527751171053-6ac5ec50000b?w=800',
        'https://images.unsplash.com/photo-1500595046783-cd2118934840?w=800'
    ],
    'Automobile': [
        'https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=800',
        'https://images.unsplash.com/photo-1517524206127-48bbd363f3d7?w=800'
    ],
    'Carpentry': [
        'https://images.unsplash.com/photo-1497366216548-37526070297c?w=800',
        'https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=800'
    ],
    'Construction': [
        'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800',
        'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800'
    ],
    'Veterinary': [
        'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800',
        'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=800'
    ],
    'Plumbing': [
        'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=800',
        'https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=800'
    ],
    'Fruits & Vegetables': [
        'https://images.unsplash.com/photo-1610832958506-ee5633613df2?w=800',
        'https://images.unsplash.com/photo-1543083503-0c3b88d75249?w=800'
    ],
    'Kitchenware': [
        'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=800',
        'https://images.unsplash.com/photo-1596797038530-2c107229654b?w=800'
    ]
};

function getShopCoverImage(shop) {
    let img = shop.imageUrl ? shop.imageUrl.trim() : '';
    if (!img) {
        const category = shop.category || 'Groceries';
        const list = CATEGORY_DEFAULT_IMAGES[category] || ['https://images.unsplash.com/photo-1534723452862-4c874018d66d?w=800'];
        const idx = (shop.shopId || 0) % list.length;
        img = list[idx];
    }
    return img;
}

